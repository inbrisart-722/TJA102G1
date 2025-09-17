package com.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// @Component // 1. 是標上這個
// 2. 是可在 @SpringBootApplication 那支 加上 @ConfigurationPropertiesScan("com.properties");

@ConfigurationProperties(prefix = "jwt")
// prefix → 對應設定檔裡的 key 前綴，例如 jwt.secret → 前綴是 jwt。
// Spring 會自動把屬性值注入到同名的欄位裡。
public record JwtProperties (
	String secret,
	String issuer,
	Duration memAccessTtl,
	Duration memRefreshTtl,
	Duration exhibAccessTtl,
	Duration exhibRefreshTtl
) {}
