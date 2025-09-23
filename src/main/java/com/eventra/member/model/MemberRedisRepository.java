package com.eventra.member.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.util.RedisPoolExecutor;

import redis.clients.jedis.Transaction;

@Repository
public class MemberRedisRepository {

	private final RedisPoolExecutor JEDIS;
	private static final Integer TOKEN_VALIDATED_TO_REGISTER = 60 * 60;
	private static final Integer TOKEN_EXPIRATION = 30 * 60;
	private static final Integer SEND_TIME_INTERVAL = 10 * 60;

	public MemberRedisRepository(RedisPoolExecutor jedis) {
		this.JEDIS = jedis;
	}
	
	private final String tokenKey(String token) {
		return "verif:token:" + token;
	}
	private final String resendLimitKey(String email) {
		return "verif:resend_limit:" + email;
	}

	public Long checkIfSendable(String email) {
		String rlKey = resendLimitKey(email);
				
		return JEDIS.execute(jedis -> {
			String ifKeyExists = jedis.get(rlKey);
			
			// 冷卻 key 已經逾時 -> 可以重寄
			if(ifKeyExists == null) return null;
			// 冷卻 key 還在 -> 回傳 long 剩餘秒數
			return jedis.ttl(rlKey);
		});
	}
	
	public String createAuthToken(Map<String, String> map) {
		String token = UUID.randomUUID().toString();
		String email = map.get("email");
		
		map.put("token", token);
		map.put("attempts", "0");

		JEDIS.execute(jedis -> {
			Transaction t = jedis.multi();
			// token, email, authType (,attempts 防 SMS Flooding, Takeover)
			t.hmset("verif:token:" + token, map);
			
			t.expire("verif:token:" + token, TOKEN_EXPIRATION);
			// 確保用戶 10 分鐘 只能發送 1 次驗證信 (1 是隨機的數字，key 才是重點）
			t.setex("verif:resend_limit:" + email, SEND_TIME_INTERVAL, "1");
			t.exec();
			return null;
		});
		return token;
	}
	
	public String verifyToken(String token) {
		
		return JEDIS.execute(jedis -> {
			boolean ifTokenExists = jedis.exists("verif:token:" + token); // boolean
			
			if(ifTokenExists == false) return null;
			
			// 幫他續時間，之後給第二步驟的註冊使用（1hr 內要完成註冊流程)
			// 每次要記得加 1 避免任意無限一直續時間，上限 3 次
			Integer attempts = Integer.valueOf(jedis.hget("verif:token:" + token, "attempts"));
			System.out.println(attempts + "********");
			
			if(attempts >= 3) {
				jedis.del("verif:token:" + token);
				return null;
			}
			else {
				jedis.hset("verif:token:" + token, "attempts", String.valueOf(attempts + 1));
				jedis.expire("verif:token:" + token, TOKEN_VALIDATED_TO_REGISTER);
				String authType= jedis.hget("verif:token:" + token, "authType");
				return authType;
			}
		});
	}
	
	// (1) register1 給 register2 會用到
	// (2) register2 送出 register 也會實際用到 token 查 email 來建 member
	public String findEmailByToken(String token) {
		return JEDIS.execute(jedis -> {
			return jedis.hget("verif:token:" + token, "email");
		});
	}
	
	public void deleteToken(String token) {
		JEDIS.execute(jedis -> jedis.del("verif:token:" + token));
	}
	
	public void deleteResendLimit(String email) {
		JEDIS.execute(jedis -> jedis.del(resendLimitKey(email)));
	}

}
