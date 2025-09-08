package com.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisPoolExecutor {

	@Autowired
    private final JedisPool jedisPool;

    public RedisPoolExecutor(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @FunctionalInterface
    public interface RedisCommand<T> {
        T apply(Jedis jedis) throws Exception;
    }

    /** 統一 getResource / close / 例外轉換 */
    public <T> T execute(RedisCommand<T> cmd) {
        try (Jedis jedis = jedisPool.getResource()) {
            return cmd.apply(jedis);
        } catch (Exception e) {
        	org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RedisPoolExecutor.class);
            log.error("Redis command failed: {}", e.getMessage(), e); // 會印出最底層 cause
            throw new RuntimeException("Redis command failed: " + e.getMessage(), e);
        }
    }

    /* overloading java.util.function.Function 寫法 */
//    public <T> T execute(Function<Jedis, T> fn) {
//        try (Jedis jedis = jedisPool.getResource()) {
//            return fn.apply(jedis);
//        }
//    }
}
