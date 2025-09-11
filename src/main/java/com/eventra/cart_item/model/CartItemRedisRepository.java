package com.eventra.cart_item.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.util.JsonCodec;
import com.util.RedisPoolExecutor;

import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

@Repository
public class CartItemRedisRepository {

//	private static final Gson GSON = new Gson();
	
	// .writeValueAsString -> .write()
	// .readValue(.. , .. .class); -> .read()
	private final JsonCodec JSON_CODEC;
	private final RedisPoolExecutor JEDIS;

	private static final Integer WEAK_TTL_IN_SECONDS = 31 * 60; // 31mins（整台車用，小 buffer）
	private static final Long TTL_IN_MILLISECONDS = 30 * 60 * 1000L; // 30mins（單筆明細用）
//	private static final 
	
	private String hKey(Integer memberId) {return "cart:items:" + memberId;}
	private String zKey(Integer memberId) {return "cart:items:exp:" + memberId;}
	
	/* 主動清理：刪掉已過期的 cartItem（讀/寫前都先呼叫一次） */
	public List<CartItemRedisVO> cleanupExpired(Integer memberId, long now) {
		String hKey = hKey(memberId);
		String zKey = zKey(memberId); 
		
		return JEDIS.execute(jedis -> {
			// 先取出到期的 cartItemId
			List<String> expiredIds = jedis.zrangeByScore(zKey, Double.NEGATIVE_INFINITY, now);
			if(expiredIds == null || expiredIds.isEmpty()) return null; // List.of();
			
			// 原子刪除 Hash/ZSet	
			Transaction t = jedis.multi();
			Response<List<String>> vals = t.hmget(hKey, expiredIds.toArray(new String[0]));
			t.hdel(hKey, expiredIds.toArray(new String[0]));
			t.zrem(zKey, expiredIds.toArray(new String[0]));
		    t.exec();
		    List<String> listOfVOJsons = vals.get();
		    
		    if(listOfVOJsons == null || listOfVOJsons.isEmpty()) return null;
		    
		    // 以防 id 沒找到 VOJson
		    List<CartItemRedisVO> listOfVOs = new ArrayList<>();
		    for (String json : listOfVOJsons) {
	            if (json == null || json.isEmpty()) continue;
	             listOfVOs.add(JSON_CODEC.read(json, CartItemRedisVO.class));
	        }
//		    List<CartItemRedisVO> listOfVOs = listOfVOJsons.stream().map(json -> JSON_CODEC.read(json, CartItemRedisVO.class)).collect(Collectors.toList());
		    return listOfVOs;
		});
	}
	
	
	public CartItemRedisRepository(RedisPoolExecutor jedis, JsonCodec jsonCodec) {
		this.JEDIS = jedis;
		this.JSON_CODEC = jsonCodec;
	}
	
	
	// Redis PK
	public Long getCartItemId() {
		return JEDIS.execute(jedis -> jedis.incr("cart:item:id:counter"));
	}
	
	public void addCartItem(CartItemRedisVO vo) {
		String cartItemId = String.valueOf(vo.getCartItemId());
		Integer memberId = vo.getMemberId();
		Long createdAt = vo.getCreatedAt();
		Long expireAt = createdAt + TTL_IN_MILLISECONDS; // 用在 zset
		String jsonVO = JSON_CODEC.write(vo);
		
		String hKey = hKey(memberId);
		String zKey = zKey(memberId); 

		JEDIS.execute(jedis -> {
			Transaction t = jedis.multi();
			t.hset(hKey, cartItemId, jsonVO);
			t.zadd(zKey, expireAt, cartItemId);

			// 1. 重設很便宜 2. 此處為唯一！只有寫入會續命弱 TTL
			t.expire(hKey, WEAK_TTL_IN_SECONDS);
			t.expire(zKey , WEAK_TTL_IN_SECONDS);
			t.exec();
			return null;
		});
	}

	// removeCartItem -> overloading 1: 刪除多筆
	public List<CartItemRedisVO> removeCartItem(Collection<Integer> cartItemIds, Integer memberId) {
		String hKey = hKey(memberId);
		String zKey = zKey(memberId); 
		
		String[] ids = cartItemIds.stream().map(Object::toString).collect(Collectors.toList()).toArray(new String[0]);
		
		return JEDIS.execute(jedis -> {
			// MULTI + EXEC 主要是為了打包命令以節省網路往返次數，並確保命令序列不會被其他命令中斷。但它不提供隔離性...
			Transaction t = jedis.multi();
			// 先拿 Response<T> 代理物件 等待 exec() 後 .get() 取值
			Response<List<String>> r1 = t.hmget(hKey, ids);
			// 以下皆為回傳被刪除 rows 之數量
			t.hdel(hKey, ids);
			t.zrem(zKey, ids);
			t.exec();
			
			List<String> listOfVOJsons = r1.get();
			if(listOfVOJsons == null || listOfVOJsons.isEmpty()) return null;
			List<CartItemRedisVO> listOfVOs = new ArrayList<>(); 
			for(String VOJson : listOfVOJsons) {
				if(VOJson == null || VOJson.isEmpty()) continue;
				listOfVOs.add(JSON_CODEC.read(VOJson, CartItemRedisVO.class));
			}
			return listOfVOs;
		});
	}
	// removeCartItem -> overloading 2: 刪除單筆
		public CartItemRedisVO removeOneCartItem(Integer cartItemId, Integer memberId) {
		String hKey = hKey(memberId);
		String zKey = zKey(memberId); 
		
		return JEDIS.execute(jedis -> {
			// MULTI + EXEC 主要是為了打包命令以節省網路往返次數，並確保命令序列不會被其他命令中斷。但它不提供隔離性...
			Transaction t = jedis.multi();
			// 先拿 Response<T> 代理物件 等待 exec() 後 .get() 取值
			Response<String> r1 = t.hget(hKey, String.valueOf(cartItemId));
			// 以下皆為回傳被刪除 rows 之數量
			t.hdel(hKey, String.valueOf(cartItemId));
			t.zrem(zKey, String.valueOf(cartItemId));
			t.exec();
			
			String VOJson = r1.get();
			if(VOJson == null || VOJson.isEmpty()) return null;
			else return JSON_CODEC.read(VOJson, CartItemRedisVO.class);
		});
	}

	public List<CartItemRedisVO> removeAllCartItem(Integer memberId) {
		String hKey = hKey(memberId);
		String zKey = zKey(memberId);
		
		return JEDIS.execute(jedis -> {
			Transaction t = jedis.multi();
			Response<List<String>> vals = t.hvals("cart:items:" + memberId);
			t.del(hKey);
			t.del(zKey);
			t.exec();
			
			List<String> listOfVOJsons = vals.get();
			
			if(listOfVOJsons == null || listOfVOJsons.isEmpty()) return null;
			List<CartItemRedisVO> listOfVOs = new ArrayList<>(); 
			for(String VOJson : listOfVOJsons) {
				if(VOJson == null || VOJson.isEmpty()) continue;
				listOfVOs.add(JSON_CODEC.read(VOJson, CartItemRedisVO.class));
			}
			return listOfVOs;
		});
	}

	public CartItemRedisVO getOneCartItem(Integer memberId, Integer cartItemId) {
		String hKey = hKey(memberId);
		
		String VOJson = JEDIS.execute(
				jedis -> jedis.hget(hKey, String.valueOf(cartItemId)));

		return JSON_CODEC.read(VOJson, CartItemRedisVO.class);
	}

	public List<CartItemRedisVO> getAllCartItem(Integer memberId) {
		String hKey = hKey(memberId);
		String zKey = zKey(memberId);
		
		List<String> listOfIds = JEDIS.execute(jedis -> jedis.zrevrange(zKey, 0, -1)); // 愈快過期愈上方（也可從前端影響）
		String[] arrayOfIds = listOfIds.toArray(new String[listOfIds.size()]);

		if (arrayOfIds.length == 0) {
			return null;
		}

		List<String> listOfVOJsons = JEDIS.execute(jedis -> jedis.hmget(hKey, arrayOfIds));
		
		List<CartItemRedisVO> listOfVOs = listOfVOJsons.stream().map(json -> JSON_CODEC.read(json, CartItemRedisVO.class))
				.collect(Collectors.toList());

		return listOfVOs;
	}

	public CartItemRedisVO getEarliestCartItem(Integer memberId) {
		String hKey = hKey(memberId);
		String zKey = zKey(memberId);
		
		List<String> list = JEDIS.execute(jedis -> jedis.zrange(zKey, 0, 0));
		
		if (list == null || list.isEmpty()) return null;
		
		String cartItemId = list.iterator().next();
		CartItemRedisVO cartItemRedisVO = 
				JSON_CODEC.read(JEDIS.execute(jedis -> jedis.hget(hKey, cartItemId)), CartItemRedisVO.class);
		
		return cartItemRedisVO;
	}
}
