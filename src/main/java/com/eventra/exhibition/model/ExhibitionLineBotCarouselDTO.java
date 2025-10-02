package com.eventra.exhibition.model;

import java.time.LocalDateTime;

public class ExhibitionLineBotCarouselDTO {
	private String exhibitionName;
	private String photoPortrait;
	private Double averageRatingScore;
	private String location;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String pageUrl; // ".../front-end/exhibitions?exhibitionId=" + exhibitionId 即可
	
	public String getExhibitionName() {
		return exhibitionName;
	}
	public ExhibitionLineBotCarouselDTO setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
		return this;
	}
	public String getPhotoPortrait() {
		return photoPortrait;
	}
	public ExhibitionLineBotCarouselDTO setPhotoPortrait(String photoPortrait) {
		this.photoPortrait = photoPortrait;
		return this;
	}
	public Double getAverageRatingScore() {
		return averageRatingScore;
	}
	public ExhibitionLineBotCarouselDTO setAverageRatingScore(Double averageRatingScore) {
		this.averageRatingScore = averageRatingScore;
		return this;
	}
	public String getLocation() {
		return location;
	}
	public ExhibitionLineBotCarouselDTO setLocation(String location) {
		this.location = location;
		return this;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public ExhibitionLineBotCarouselDTO setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
		return this;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public ExhibitionLineBotCarouselDTO setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
		return this;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public ExhibitionLineBotCarouselDTO setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
		return this;
	}
}
