package com.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

@Configuration
public class GoogleAuthConfig {

	@Value("${google.auth.client-id}")
	private String clientId;

	@Bean
	public GoogleIdTokenVerifier googleIdTokenVerifier() {
		// 1. transport 是 Google API client 裡抽象的 HTTP client，最常用的實作是 NetHttpTransport：
		//    它內部就是基於 Java 的 HTTP client，Google library 會用它去 call https://www.googleapis.com/oauth2/v3/certs 之類的 URL 抓公鑰。
		HttpTransport transport = new NetHttpTransport();
		// 2. jsonFactory 是 Google API client 用來 parse JSON 的介面。
		//    最常見的實作有兩種： JacksonFactory (用 Jackson 做底層); GsonFactory (用 Gson 做底層)
		JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
		// -> 靜態方法，回傳一個 singleton（單例物件） 的 GsonFactory。
		// -> GsonFactory 是 thread-safe，可以多線程共用一個 instance。

		return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				.setAudience(Collections.singletonList(clientId)).build();
	}
}
