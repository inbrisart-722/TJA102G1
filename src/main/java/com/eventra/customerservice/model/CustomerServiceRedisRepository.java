package com.eventra.customerservice.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.util.JsonCodec;
import com.util.RedisPoolExecutor;

@Repository
public class CustomerServiceRedisRepository {

	private final JsonCodec JSON_CODEC;
	private final RedisPoolExecutor JEDIS;
	
	private static final String MSG_KEY = "chat:global:messages";
	private static final String USER_KEY = "chat:global:users"; 
	
	private static final Long TTL_MILLISECS_MSG_EXPIRE = 3 * 24 * 60 * 60 * 1000L; // 3 days
	
	public CustomerServiceRedisRepository(JsonCodec jsonCodec, RedisPoolExecutor jedis) {
		this.JSON_CODEC = jsonCodec;
		this.JEDIS = jedis;
	}
	
	// 排程 每10mins 掃 超過 3 天的 msgs 全刪除
	
	public void addMessage(ChatMessageResDTO res) {
		String json = JSON_CODEC.write(res);
		Long expire = res.getSentTime() + TTL_MILLISECS_MSG_EXPIRE;
		JEDIS.execute(jedis -> {
			jedis.zadd(MSG_KEY, expire, json);
			return null;
		});
	}
	
	public List<ChatMessageResDTO> getMessages(Long now) {
		List<String> jsons =  JEDIS.execute(jedis -> {
			// key, max, min, offset, limit
			return jedis.zrevrangeByScore(MSG_KEY, now + TTL_MILLISECS_MSG_EXPIRE - 1L, Double.NEGATIVE_INFINITY, 0, 10);
		});
		
		if(jsons == null || jsons.isEmpty()) return null;
		
		List<ChatMessageResDTO> msgs = new ArrayList<>();
		for(String json : jsons){
			if(json != null && !json.isBlank()) {
				msgs.add(JSON_CODEC.read(json, ChatMessageResDTO.class));
			}
		}
		
		Collections.reverse(msgs);
		
		return msgs;
	}
}
