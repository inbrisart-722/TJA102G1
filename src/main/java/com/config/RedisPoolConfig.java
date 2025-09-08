package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

@Configuration
public class RedisPoolConfig {

    @Bean(destroyMethod = "close")
    public JedisPool jedisPool(RedisPoolProperties props) {
        JedisPoolConfig poolCfg = new JedisPoolConfig();
        poolCfg.setMaxTotal(props.getMaxTotal());
        poolCfg.setMaxIdle(props.getMaxIdle());
        poolCfg.setMinIdle(props.getMinIdle());
        poolCfg.setBlockWhenExhausted(props.isBlockWhenExhausted());
        poolCfg.setMaxWaitMillis(props.getMaxWaitMillis());
        poolCfg.setTestOnBorrow(props.isTestOnBorrow());
        poolCfg.setTestOnReturn(props.isTestOnReturn());
        poolCfg.setTestWhileIdle(props.isTestWhileIdle());
        poolCfg.setTestOnCreate(true);
        poolCfg.setMinEvictableIdleTimeMillis(props.getMinEvictableIdleTimeMillis());
        poolCfg.setTimeBetweenEvictionRunsMillis(props.getTimeBetweenEvictionRunsMillis());

        DefaultJedisClientConfig clientCfg = DefaultJedisClientConfig.builder()
                .user(emptyToNull(props.getUsername()))
                .password(emptyToNull(props.getPassword()))
                .socketTimeoutMillis(props.getTimeout())
                .connectionTimeoutMillis(props.getTimeout())
                .ssl(props.isSsl())
                .build();

        return new JedisPool(poolCfg, new HostAndPort(props.getHost(), props.getPort()), clientCfg);
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }
}
