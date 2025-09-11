package com.eventra.rating.model;

public class GetMyRatingResDTO {
	private String status;
	private boolean canRate;
	private Byte originalRating;
	
	public String getStatus() {
		return status;
	}
	public GetMyRatingResDTO setStatus(String status) {
		this.status = status;
		return this;
	}
	public boolean isCanRate() {
		return canRate;
	}
	public GetMyRatingResDTO setCanRate(boolean canRate) {
		this.canRate = canRate;
		return this;
	}
	public Byte getOriginalRating() {
		return originalRating;
	}
	public GetMyRatingResDTO setOriginalRating(Byte originalRating) {
		this.originalRating = originalRating;
		return this;
	}
}	
