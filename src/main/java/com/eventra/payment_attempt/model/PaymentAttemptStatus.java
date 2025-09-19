package com.eventra.payment_attempt.model;

public enum PaymentAttemptStatus {
	PENDING(1),
	EXPIRED(2),
	SUCCESS(3),
 	FAILURE(4);
	
	private final int code;
	
	PaymentAttemptStatus(int code) { this.code = code; }
	
	public int getCode() { return code; }
}
