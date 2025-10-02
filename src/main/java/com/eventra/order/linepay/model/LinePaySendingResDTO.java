package com.eventra.order.linepay.model;

public class LinePaySendingResDTO {
	private LinePaySendingStatus status;
	private String message;
	
	public LinePaySendingStatus getStatus() {
		return status;
	}
	public LinePaySendingResDTO setStatus(LinePaySendingStatus status) {
		this.status = status;
		return this;
	}
	public String getMessage() {
		return message;
	}
	public LinePaySendingResDTO setMessage(String message) {
		this.message = message;
		return this;
	}
}
