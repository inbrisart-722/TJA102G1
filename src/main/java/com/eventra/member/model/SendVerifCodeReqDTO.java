package com.eventra.member.model;

public class SendVerifCodeReqDTO {
	private String email; 
	private String authType;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAuthType() {
		return authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
}
