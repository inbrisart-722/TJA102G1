package com.eventra.cart_item.model;

public class GetMyExpirationResDTO {
	private String status;
	private String backgroundExpireTime;
	
	public String getStatus() {
		return status;
	}
	public GetMyExpirationResDTO setStatus(String status) {
		this.status = status;
		return this;
	}
	public String getBackgroundExpireTime() {
		return backgroundExpireTime;
	}
	public GetMyExpirationResDTO setBackgroundExpireTime(String backgroundExpireTime) {
		this.backgroundExpireTime = backgroundExpireTime;
		return this;
	}
}
