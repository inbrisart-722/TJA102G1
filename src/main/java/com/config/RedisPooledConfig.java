//package com.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import redis.clients.jedis.DefaultJedisClientConfig;
//import redis.clients.jedis.HostAndPort;
//import redis.clients.jedis.JedisPooled;
//
//@Configuration
//public class RedisPooledConfig {
//
//    @Bean(destroyMethod = "close")
//    public JedisPooled jedis(RedisPooledProperties props) {
//        // 建立 client 設定（支援 username/password/SSL）
//        DefaultJedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
//                .user(emptyToNull(props.getUsername()))
//                .password(emptyToNull(props.getPassword()))
//                .ssl(props.isSsl())
//                .build();
//
//        // JedisPooled 會自帶連線池；不需手動關閉每次連線
//        return new JedisPooled(new HostAndPort(props.getHost(), props.getPort()), clientConfig);
//    }
//
//    private static String emptyToNull(String s) {
//        return (s == null || s.isEmpty()) ? null : s;
//    }
//}
