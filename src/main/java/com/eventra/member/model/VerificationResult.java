package com.eventra.member.model;

public enum VerificationResult {
	SUCCESS("驗證成功！"),
	TOKEN_NOT_FOUND("此 TOKEN 不存在、已過期或使用次數已達上限"),
	TOKEN_TYPE_INVALID("此 TOKEN 不適用於 REGISTRATION 用途");
	
	private final String message;
	
	VerificationResult(String message){
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
