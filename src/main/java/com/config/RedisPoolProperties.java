package com.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.redis")
public class RedisPoolProperties {
    // 基本連線
    private String host;
    private int port;
    private int timeout;
    private boolean ssl;
    private String username;
    private String password;

    // pool 參數
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private boolean blockWhenExhausted;
    private long maxWaitMillis;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean testWhileIdle;
    private long minEvictableIdleTimeMillis;
    private long timeBetweenEvictionRunsMillis;

    // getter & setter ...
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public boolean isSsl() { return ssl; }
    public void setSsl(boolean ssl) { this.ssl = ssl; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMaxTotal() { return maxTotal; }
    public void setMaxTotal(int maxTotal) { this.maxTotal = maxTotal; }
    public int getMaxIdle() { return maxIdle; }
    public void setMaxIdle(int maxIdle) { this.maxIdle = maxIdle; }
    public int getMinIdle() { return minIdle; }
    public void setMinIdle(int minIdle) { this.minIdle = minIdle; }
    public boolean isBlockWhenExhausted() { return blockWhenExhausted; }
    public void setBlockWhenExhausted(boolean blockWhenExhausted) { this.blockWhenExhausted = blockWhenExhausted; }
    public long getMaxWaitMillis() { return maxWaitMillis; }
    public void setMaxWaitMillis(long maxWaitMillis) { this.maxWaitMillis = maxWaitMillis; }
    public boolean isTestOnBorrow() { return testOnBorrow; }
    public void setTestOnBorrow(boolean testOnBorrow) { this.testOnBorrow = testOnBorrow; }
    public boolean isTestOnReturn() { return testOnReturn; }
    public void setTestOnReturn(boolean testOnReturn) { this.testOnReturn = testOnReturn; }
    public boolean isTestWhileIdle() { return testWhileIdle; }
    public void setTestWhileIdle(boolean testWhileIdle) { this.testWhileIdle = testWhileIdle; }
    public long getMinEvictableIdleTimeMillis() { return minEvictableIdleTimeMillis; }
    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) { this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis; }
    public long getTimeBetweenEvictionRunsMillis() { return timeBetweenEvictionRunsMillis; }
    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) { this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis; }
}
