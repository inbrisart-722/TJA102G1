package com.eventra.exhibitioncommon.dto;

import java.time.LocalDateTime;

public class ExhibitionListDTO {
    private Integer exhibitionId;		// 展覽id
    private String exhibitionName;		// 展覽名稱
    private String photoLandscape;		// 展覽圖片
    private Integer minPrice;			// 最低票價
    private Integer maxPrice;			// 最高票價
    private LocalDateTime startTime;   	// 展覽開始時間
    private LocalDateTime endTime;     	// 展覽結束時間
    private String location;			// 地點
    private Integer totalViews;			// 展覽頁點擊總數 (熱門排序用)
    private Integer ratingCount;		// 評價總數
    private Double averageRatingScore;  // 平均分數
    
    // 無參數建構子
    public ExhibitionListDTO() {}

    // 有參數建構子
	public ExhibitionListDTO(Integer exhibitionId, String exhibitionName, String photoLandscape, Integer minPrice,
			Integer maxPrice, LocalDateTime startTime, LocalDateTime endTime, String location, Integer totalViews,
			Integer ratingCount, Double averageRatingScore) {
		super();
		this.exhibitionId = exhibitionId;
		this.exhibitionName = exhibitionName;
		this.photoLandscape = photoLandscape;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
		this.totalViews = totalViews;
		this.ratingCount = ratingCount;
		this.averageRatingScore = averageRatingScore;
	}

	//getter/setter
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

	public String getPhotoLandscape() {
		return photoLandscape;
	}

	public void setPhotoLandscape(String photoLandscape) {
		this.photoLandscape = photoLandscape;
	}

	public Integer getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public Integer getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getTotalViews() {
		return totalViews;
	}

	public void setTotalViews(Integer totalViews) {
		this.totalViews = totalViews;
	}

	public Integer getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}

	public Double getAverageRatingScore() {
		return averageRatingScore;
	}

	public void setAverageRatingScore(Double averageRatingScore) {
		this.averageRatingScore = averageRatingScore;
	}
	
}
