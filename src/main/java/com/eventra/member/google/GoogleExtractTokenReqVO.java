package com.eventra.member.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleExtractTokenReqVO {

	// JsonProperty 也可以使用
	
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("expires_in")
	private long expiresIn;
	@JsonProperty("refresh_token")
	private String refreshToken;
	@JsonProperty("scope")
	private String scope;
	@JsonProperty("id_token")
	private String idToken;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getIdToken() {
		return idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
	

	
//	{
//		  "access_token": "ya29.a0AfH6SMD...",
//		  "expires_in": 3599,
//		  "refresh_token": "1//0gZC...",
//		  "scope": "openid email profile",
//		  "id_token": "eyJhbGciOiJSUzI1NiIsImtpZCI6..."
//	}
	
	// 驗證 id_token → 取得使用者資訊
	// id_token 是一顆 JWT，裡面含有 Google 的使用者資訊，例如：

//	{
//		  "iss": "https://accounts.google.com",
//		  "sub": "110169484474386276334",
//		  "email": "user@gmail.com",
//		  "email_verified": true,
//		  "name": "User Name",
//		  "picture": "https://lh3.googleusercontent.com/a-/AOh14Gg..."
//	}

}
