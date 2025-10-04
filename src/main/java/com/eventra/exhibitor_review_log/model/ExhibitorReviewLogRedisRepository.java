package com.eventra.exhibitor_review_log.model;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.util.JsonCodec;
import com.util.RedisPoolExecutor;

@Repository
public class ExhibitorReviewLogRedisRepository {

	private final JsonCodec JSON_CODEC;
	private final RedisPoolExecutor JEDIS;
	
	public ExhibitorReviewLogRedisRepository(JsonCodec jsonCodec, RedisPoolExecutor jedis) {
		this.JSON_CODEC = jsonCodec;
		this.JEDIS = jedis;
	}
	
	private String failureTokenKey(String token) {
		return "exhibitor:register:failure:" + token; 
	}
	// exhibitor:register:failure:{token} exhibitorId
	// basic String type
	// no TTL
	
	public void deleteFailureToken(String token) {
		
		String key = failureTokenKey(token);
		
		JEDIS.execute(jedis -> {
			jedis.del(key);
			return null;
		});
	}
	
	public String saveFailureToken(Integer exhibitorId) {
		
		String token = UUID.randomUUID().toString();
		String key = failureTokenKey(token);
				
		JEDIS.execute(jedis -> {
			jedis.set(key, exhibitorId.toString());
			return null;
		});
		
		return token;
	}
	
	public Integer getExhibitorIdFromToken(String token) {
		
		String key = failureTokenKey(token);
		
		// 不刪，可重複利用直到註冊成功！
		return JEDIS.execute(jedis -> {
			String exhibitorId = jedis.get(key);
			if (exhibitorId == null || exhibitorId.isBlank()) return null;
			else return Integer.valueOf(exhibitorId);
		});
	}
}
