package com.eventra.exhibition.model;

import java.time.LocalDateTime;

public class ExhibitionSidebarDTO {
	private Integer exhibitionId;
	private String photoPortrait;
	private String exhibitionName;
	private String location;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Double averageRatingScore;
	private Integer totalRatingCount;
	
	
	public Integer getExhibitionId() {
		return exhibitionId;
	}
	public ExhibitionSidebarDTO setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
		return this;
	}
	public String getPhotoPortrait() {
		return photoPortrait;
	}
	public ExhibitionSidebarDTO setPhotoPortrait(String photoPortrait) {
		this.photoPortrait = photoPortrait;
		return this;
	}
	public String getExhibitionName() {
		return exhibitionName;
	}
	public ExhibitionSidebarDTO setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
		return this;
	}
	public String getLocation() {
		return location;
	}
	public ExhibitionSidebarDTO setLocation(String location) {
		this.location = location;
		return this;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public ExhibitionSidebarDTO setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
		return this;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public ExhibitionSidebarDTO setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
		return this;
	}
	public Double getAverageRatingScore() {
		return averageRatingScore;
	}
	public ExhibitionSidebarDTO setAverageRatingScore(Double averageRatingScore) {
		this.averageRatingScore = averageRatingScore;
		return this;
	}
	public Integer getTotalRatingCount() {
		return totalRatingCount;
	}
	public ExhibitionSidebarDTO setTotalRatingCount(Integer totalRatingCount) {
		this.totalRatingCount = totalRatingCount;
		return this;
	}
}
