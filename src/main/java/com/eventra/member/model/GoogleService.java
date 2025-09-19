package com.eventra.member.model;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.properties.GoogleAuthProperties;

@Service
@Transactional
public class GoogleService {

	private final RestClient REST_CLIENT;
	private final GoogleIdTokenVerifier VERIFIER;
	private final GoogleAuthProperties GOOGLE_AUTH_PROPS;
	
	public GoogleService(GoogleAuthProperties googleAuthProperties, RestClient.Builder restClientBuilder, GoogleIdTokenVerifier verifier) {
		this.GOOGLE_AUTH_PROPS = googleAuthProperties;
		this.REST_CLIENT = restClientBuilder.baseUrl(googleAuthProperties.urlCodeToToken())
				.build();
		this.VERIFIER = verifier;
	}
	
	public String extractToken(String code) {
		
		MultiValueMap<String, String> map = fieldsBuilder(code);
		
		ExtractTokenReqVO token = REST_CLIENT
				.post() // method: "POST"
				.contentType(MediaType.APPLICATION_FORM_URLENCODED) // Content-Type: application/x-www-form-urlencoded
				.body(map)
				.retrieve()
				.body(ExtractTokenReqVO.class);
		
		System.out.println(token);
		
		// Google API Client Library
		// Google → id_token → 驗證成功 → [你的系統] 創建/查會員 → [你的系統] 簽發 Access/Refresh JWT → 存 Cookie → 完成登入。
		
//		ID 權杖已由 Google 正確簽署。使用 Google 的公開金鑰 (提供 JWK 或 PEM 格式)，驗證權杖的簽名。這些金鑰會定期輪替，請檢查回應中的 Cache-Control 標頭，判斷何時應再次擷取金鑰。
//		ID 權杖中的 aud 值等於應用程式的其中一個用戶端 ID。這項檢查是必要的，可防止惡意應用程式使用發給該應用程式的 ID 權杖，存取您應用程式後端伺服器上同一位使用者的資料。
//		ID 權杖中的 iss 值等於 accounts.google.com 或 https://accounts.google.com。
//		ID 權杖的到期時間 (exp) 尚未到期。
//		如要驗證 ID 權杖是否代表 Google Workspace 或 Cloud 機構帳戶，可以檢查 hd 聲明，其中會指出使用者的代管網域。如要限制只有特定網域的成員可以存取資源，就必須使用這項設定。如果沒有這項聲明，表示帳戶不屬於 Google 代管網域。
		
		String idTokenString = token.getIdToken(); // JWT 字串
		
		GoogleIdToken idToken = null;
		try {idToken = VERIFIER.verify(idTokenString);}
		catch (IOException | GeneralSecurityException e) {System.out.println(e.toString());}
		
		if (idToken != null) {
		  Payload payload = idToken.getPayload();

		  // Print user identifier
		  String userId = payload.getSubject();
		  System.out.println("User ID: " + userId);

		  // Get profile information from payload
		  String email = payload.getEmail();
		  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
		  String name = (String) payload.get("name");
		  String pictureUrl = (String) payload.get("picture");
		  String locale = (String) payload.get("locale");
		  String familyName = (String) payload.get("family_name");
		  String givenName = (String) payload.get("given_name");

		  // Use or store profile information
		  // ...

		} else {
		  System.out.println("Invalid ID token.");
		}
		
		return null;
	}
	
	private MultiValueMap<String, String> fieldsBuilder(String code) {
//		code=授權碼
//		&client_id=YOUR_CLIENT_ID
//		&client_secret=YOUR_CLIENT_SECRET
//		&redirect_uri=http://localhost:8080/auth/google/callback
//		&grant_type=authorization_code
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("code", code);
		map.add("client_id", GOOGLE_AUTH_PROPS.clientId());
		map.add("client_secret", GOOGLE_AUTH_PROPS.clientSecret());
		map.add("redirect_uri", GOOGLE_AUTH_PROPS.redirectUri());
		map.add("grant_type", GOOGLE_AUTH_PROPS.grantType());
		
		return map;
	}
	
	
}
