package com.eventra.map.dto;

import java.time.LocalDateTime;

public class MapExploreDTO {
	/* 地圖探索頁 展覽資訊卡片用 */
	
	private Integer exhibitionId;		// 展覽ID
	private String exhibitionName;		// 展覽名稱
	private String location;			// 展覽地點
	private Double longitude;			// 經度
	private Double latitude;			// 緯度
	private String photo;				// 展覽圖片
	private LocalDateTime startTime;	// 開始時間
	private LocalDateTime endTime;		// 結束時間
	private Integer ratingCount;		// 評價總數
	
	// 無參數建構子
	public MapExploreDTO() {
		super();
	}
	
	// 有參數建構子
	public MapExploreDTO(Integer exhibitionId, String exhibitionName, String location, Double longitude,
			Double latitude, String photo, LocalDateTime startTime, LocalDateTime endTime, Integer ratingCount) {
		super();
		this.exhibitionId = exhibitionId;
		this.exhibitionName = exhibitionName;
		this.location = location;
		this.longitude = longitude;
		this.latitude = latitude;
		this.photo = photo;
		this.startTime = startTime;
		this.endTime = endTime;
		this.ratingCount = ratingCount;
	}

	// getter / setter
	public Integer getExhibitionId() {
		return exhibitionId;
	}

	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}

	public String getExhibitionName() {
		return exhibitionName;
	}

	public void setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Integer getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}
	
}
