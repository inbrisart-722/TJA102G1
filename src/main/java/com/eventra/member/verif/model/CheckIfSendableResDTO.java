package com.eventra.member.verif.model;

public class CheckIfSendableResDTO {
	private Boolean allowed;
	private Long remaining;
	
	public Boolean isAllowed() {
		return allowed;
	}
	public CheckIfSendableResDTO setAllowed(Boolean allowed) {
		this.allowed = allowed;
		return this;
	}
	public Long getRemaining() {
		return remaining;
	}
	public CheckIfSendableResDTO setRemaining(Long remaining) {
		this.remaining = remaining;
		return this;
	}
}
