package com.eventra.member.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitHubExtractTokenReqDTO {
	@JsonProperty("access_token")
	private String accessToken;
	
	private String scope;
	
	@JsonProperty("token_type")
	private String tokenType;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	
}
