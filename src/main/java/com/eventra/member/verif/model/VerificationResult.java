package com.eventra.member.verif.model;

public enum VerificationResult {
	SUCCESS("驗證成功！"),
	TOKEN_NOT_FOUND("此 TOKEN 不存在、已過期或使用次數已達上限"),
	TOKEN_TYPE_INVALID_REGISTRATION("此 TOKEN 不適用於 註冊 之用途"),
	TOKEN_TYPE_INVALID_FORGOT_PASSWORD("此 TOKEN 不適用於 忘記密碼 之用途"),
	TOKEN_TYPE_INVALID_CHANGE_MAIL("此 TOKEN 不適用於 會員更換信箱 之用途");
	
	private final String message;
	
	VerificationResult(String message){
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
