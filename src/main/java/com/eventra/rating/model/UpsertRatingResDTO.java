package com.eventra.rating.model;

public class UpsertRatingResDTO {
	private String status;
	private Integer totalRatingCount;
	private Double averageRatingScore;
	
	public String getStatus() {
		return status;
	}
	public UpsertRatingResDTO setStatus(String status) {
		this.status = status;
		return this;
	}
	public Integer getTotalRatingCount() {
		return totalRatingCount;
	}
	public UpsertRatingResDTO setTotalRatingCount(Integer totalRatingCount) {
		this.totalRatingCount = totalRatingCount;
		return this;
	}
	public Double getAverageRatingScore() {
		return averageRatingScore;
//		return Math.round(averageRatingScore * 10) / 10.0;
	}
	public UpsertRatingResDTO setAverageRatingScore(Double averageRatingScore) {
		this.averageRatingScore = averageRatingScore;
		return this;
	}
}
