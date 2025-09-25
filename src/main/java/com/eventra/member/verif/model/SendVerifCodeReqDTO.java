package com.eventra.member.verif.model;

public class SendVerifCodeReqDTO {
	private String email; 
	private AuthType authType;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public AuthType getAuthType() {
		return authType;
	}
	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}
}
