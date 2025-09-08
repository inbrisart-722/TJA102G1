//package com.config;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "app.redised")
//public class RedisPooledProperties {
//    private String host = "localhost";
//    private int port = 6379;
//    private String username;   // 大多數情境不用
//    private String password;   // 若無可留空
//    private boolean ssl = false;
//
//    // getters & setters
//    public String getHost() { return host; }
//    public void setHost(String host) { this.host = host; }
//    public int getPort() { return port; }
//    public void setPort(int port) { this.port = port; }
//    public String getUsername() { return username; }
//    public void setUsername(String username) { this.username = username; }
//    public String getPassword() { return password; }
//    public void setPassword(String password) { this.password = password; }
//    public boolean isSsl() { return ssl; }
//    public void setSsl(boolean ssl) { this.ssl = ssl; }
//}
