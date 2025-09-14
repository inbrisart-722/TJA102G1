package com.eventra.exhibitionpagepopularitystats.dto;

import java.sql.Timestamp;

public class ExhibitionPagePopularityStatsDTO {
    private Integer exhibitionId; 			// 展覽ID
    private String exhibitionName; 			// 展覽名稱
    private String photoLandscape; 			// 圖片
    private Timestamp startTime; 			// 展覽開始時間
    private Timestamp endTime; 				// 展覽結束時間
    private String location; 				// 展覽地點
//    private Double averageRatingScore; 	// 平均評價
//    private Integer ratingCount; 			// 評價總計
    private Integer totalViews; 			// sum() 處理排序用
    
    // 無參數建構子
    public ExhibitionPagePopularityStatsDTO() {
    	super();
    }
    
    // 有參數建構子
    public ExhibitionPagePopularityStatsDTO(Integer exhibitionId, String exhibitionName, String photoLandscape,
    		Timestamp startTime, Timestamp endTime, String location, Integer totalViews) {
    	// Double averageRatingScore, Integer ratingCount,
    	super();
    	this.exhibitionId = exhibitionId;
    	this.exhibitionName = exhibitionName;
    	this.photoLandscape = photoLandscape;
    	this.startTime = startTime;
    	this.endTime = endTime;
    	this.location = location;
//    	this.averageRatingScore = averageRatingScore;
//    	this.ratingCount = ratingCount;
    	this.totalViews = totalViews;
    }
    
    // getter/setter
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

	public Integer getTotalViews() {
		return totalViews;
	}

	public void setTotalViews(Integer totalViews) {
		this.totalViews = totalViews;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

//	public Double getAverageRatingScore() {
//		return averageRatingScore;
//	}
//
//	public void setAverageRatingScore(Double averageRatingScore) {
//		this.averageRatingScore = averageRatingScore;
//	}
//
//	public Integer getRatingCount() {
//		return ratingCount;
//	}
//
//	public void setRatingCount(Integer ratingCount) {
//		this.ratingCount = ratingCount;
//	}
}