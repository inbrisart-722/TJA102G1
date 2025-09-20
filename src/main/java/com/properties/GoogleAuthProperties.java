//package com.properties;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//@ConfigurationProperties(prefix = "google.auth")
//public record GoogleAuthProperties(
//
//	// 儘管彈性綁定理論上支持，但在 record 的建構子綁定模式下，對於像 String 這樣沒有特殊轉換邏輯的類型，匹配過程會更為嚴格，導致綁定失敗。
//	// ??
//
//	String clientId,
//	String clientSecret,
//	String urlCodeToToken,
//	String redirectUri,
//	String grantType
//	
//) {}
