package com.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// @Component // 1. 是標上這個
// 2. 是可在 @SpringBootApplication 那支 加上 @ConfigurationPropertiesScan("com.properties");

@ConfigurationProperties(prefix = "ecpay")
public record ECPayProperties ( // Spring Boot 2.6+ / Java 16+
	// @ConfigurationProperties 的原理是 Spring Boot 在啟動時，透過 Binder 把 application.properties / application.yml 的值 注入到物件實例。
	// 如果你用 final 或 static：
	// final → 代表這個欄位必須在建構時初始化，Spring 就沒辦法透過 setter/反射注入。只能透過建構子注入
	// static → 屬於 class 而不是 instance，Spring 管理的是 bean 實例，只會幫你注入到物件欄位上。
		String proxyHost,
	    String hashKey,
	    String hashIv,
	    String merchantId,
	    String paymentType,
	    String tradeDesc,
	    String choosePayment,
	    String encryptType,
	    String queryUrl,
	    String checkoutUrl,
	    String returnUrl,
	    String clientBackUrlPrefix,
	    String orderResultUrlPrefix
) {}
