package com.eventra.cart_item.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.util.JsonCodec;
import com.util.RedisPoolExecutor;

import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.resps.Tuple;

@Repository
public class CartItemRedisRepository {

//	private static final Gson GSON = new Gson();
	
	// .writeValueAsString -> .write()
	// .readValue(.. , .. .class); -> .read()
	private final JsonCodec JSON_CODEC;
	private final RedisPoolExecutor JEDIS;

	private static final Integer WEAK_TTL_IN_SECONDS = 6 * 60; // 6mins（整台車用，小 buffer）
	private static final Long TTL_IN_MILLISECONDS = 5 * 60 * 1000L; // 5mins（單筆明細用）
	private static final Long ABOUT_TO_EXPIRE_IN_MILLISECONDS = 295 * 1000L; // 4:55mins (5分鐘後過期通知用）
	
	private static final String GLOBAL_EXP_KEY = "cart:items:globalExp";
	private static final String NOTIFY_LOCK_KEY_PREFIX = "cart:notify:lock:";
	
	private String hKey(Integer memberId) {return "cart:items:" + memberId;}
	private String zKey(Integer memberId) {return "cart:items:exp:" + memberId;}
	
	// cleanup Global Exp 不然會有很多靠著 ttl 過期的會員 id 留在裡面...
	
	public void cleanupGlobalExp() {
		long now = System.currentTimeMillis();
				
		JEDIS.execute(jedis -> {
			List<Tuple> expiredEntries = jedis.zrangeByScoreWithScores(GLOBAL_EXP_KEY, Double.NEGATIVE_INFINITY, now);
			
			for(Tuple entry : expiredEntries) {
				String memberId = entry.getElement();
				String zKey = zKey(Integer.valueOf(memberId));
				
				if(!jedis.exists(zKey) || jedis.zcard(zKey) == 0) {
					// 該會員購物車已空 → 從 global 移除
	                jedis.zrem(GLOBAL_EXP_KEY, memberId);
	                System.out.println("清理 globalExp: 移除 memberId=" + memberId);
				} else {
					// 更新 score 為該會員最早的 expiration
					List<Tuple> minExp = jedis.zrangeWithScores(zKey, 0, 0);
	                if (!minExp.isEmpty()) {
	                    double newExpire = minExp.iterator().next().getScore();
	                    jedis.zadd(GLOBAL_EXP_KEY, newExpire, memberId);
	                    System.out.println("更新 globalExp: memberId=" + memberId + " 新expire=" + newExpire);
	                }
				}
			}
			return null;
		});
		// 找出所有過期的會員
	}
	
	// 排程通知 cart item 即將過期使用
	public List<Integer> getExpiringMemberList(long now) {
		List<String> memberIdStrs = JEDIS.execute(jedis -> {
			return jedis.zrangeByScore(GLOBAL_EXP_KEY, Double.NEGATIVE_INFINITY, now + ABOUT_TO_EXPIRE_IN_MILLISECONDS);
		});

		if(memberIdStrs == null || memberIdStrs.isEmpty()) return Collections.emptyList();
		return memberIdStrs.stream().map(Integer::valueOf).collect(Collectors.toList());
	}

	// 傳入會員清單，過濾並回傳 5 分鐘內尚未被通知過的會員清單
	public List<Integer> filterUnnotifiedMembers(List<Integer> memberIds) {
		// 如果參數根本為空 -> 直接回傳空集合
	    if (memberIds == null || memberIds.isEmpty()) return Collections.emptyList();

	    return JEDIS.execute(jedis -> {
	        List<Integer> result = new ArrayList<>();
	        // 逐一檢查每個會員
	        for (Integer memberId : memberIds) {
	            String lockKey = NOTIFY_LOCK_KEY_PREFIX + memberId;
	            // NX -> Not Exists -> 只有當此 key 不存在時才會執行
	            // EX 300 -> 這個 key 有效期間為 300 秒（5分鐘）
	            	// -> 如果 key 還不存在，設置，並且 TTL 300 秒，回傳 "OK"
	            	// -> 如果 key 存在，不做任何事，回傳 null
	            // 比起使用 SET + EXPIRE，此為原子操作更安全
	            // setex 也沒辦法配合 NX 所以不能替代。
	            String ok = jedis.set(lockKey, "1", SetParams.setParams().nx().ex(300));
	            // 在 Redis 協議中，SET 成功會回傳字串 "OK"
	            // 如果 NX 檢查沒通過，會回傳 null
	            if ("OK".equals(ok)) {
	                result.add(memberId); // 表示這次可以通知
	            }
	        }
	        return result;
	    });
	}

	
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
		    
			// *** 維護全域過期 zset
			updateGlobalExpireScoreByMemberId(zKey, memberId.toString());
		    
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
			
			// *** 維護全域過期 zset
			// 如果是第一次新增（之前沒有其他 key）
			if (jedis.zcard(zKey) == 1) {
				jedis.zadd(GLOBAL_EXP_KEY, expireAt, memberId.toString());
			}
			// 不是就不需要做事
			
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
			
			// *** 維護全域過期 zset
			updateGlobalExpireScoreByMemberId(zKey, memberId.toString());
			
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
			
			// *** 維護全域過期 zset
			updateGlobalExpireScoreByMemberId(zKey, memberId.toString());
			
			
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
			
			// *** 維護全域過期 zset
			jedis.zrem(GLOBAL_EXP_KEY, memberId.toString());
			
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
	
	public List<CartItemRedisVO> getCartItem(Integer memberId, List<Integer> cartItemIds){
		String hKey = hKey(memberId);
		
		String[] ids = cartItemIds.stream().map(String::valueOf)
				.toArray(String[]::new); 
		// constructor reference
		// size -> new String[size] 
			// -> Stream.toArray() 終端操作是 Stream API 給出 size 的
		
		List<String> listOfVOJsons = JEDIS.execute(jedis -> {
			return jedis.hmget(hKey, ids);
		});
		
		if(listOfVOJsons == null || listOfVOJsons.isEmpty()) return null;
		List<CartItemRedisVO> listOfVOs = new ArrayList<>();
		for(String VOJson : listOfVOJsons) {
			if(VOJson == null || VOJson.isEmpty()) continue;
			listOfVOs.add(JSON_CODEC.read(VOJson, CartItemRedisVO.class));
		}
		return listOfVOs;
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
	
	private void updateGlobalExpireScoreByMemberId(String zKey, String memberId) {
		// *** 維護全域過期 zset
		JEDIS.execute(jedis -> {
			List<Tuple> minItem = jedis.zrangeWithScores(zKey, 0, 0);
			if (minItem.isEmpty()) {
				// 沒有 item 了，從全域刪除
				jedis.zrem(GLOBAL_EXP_KEY, memberId);
			} else {
				// 還有 item，更新全域最早過期時間
				Tuple tuple = minItem.iterator().next();
				jedis.zadd(GLOBAL_EXP_KEY, tuple.getScore(), memberId);
			}
			return null;
		});
	}
}
