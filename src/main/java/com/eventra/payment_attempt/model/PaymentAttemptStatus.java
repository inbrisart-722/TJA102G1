package com.eventra.payment_attempt.model;

public enum PaymentAttemptStatus {
	PENDING(1),
	EXPIRED(2),
	SUCCESS(3),
 	FAILURE(4),
 	CHECKING(5); // Line Pay Confirm API 回 0000 以外的例外情境使用（目前沒有後續處理，但是 payment attempt 先設定為 checking 確保不會 order 被掃成逾時）
	
	private final int code;
	
	PaymentAttemptStatus(int code) { this.code = code; }
	
	public int getCode() { return code; }
}
