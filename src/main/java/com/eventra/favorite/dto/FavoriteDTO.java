package com.eventra.favorite.dto;

public class FavoriteDTO {
    private Integer exhibitionId;		// 展覽id
    private String exhibitionName;  	// 展覽名稱
    private boolean favoriteStatus;		// 收藏狀態
    private Double averageRatingScore;  // 平均評分
    private Integer totalRatingCount;   // 總評價數
    
    // 無參數建構子
	public FavoriteDTO() {
		super();
	}

	// 有參數建構子
	public FavoriteDTO(Integer exhibitionId, String exhibitionName, boolean favoriteStatus, Double averageRatingScore,
			Integer totalRatingCount) {
		super();
		this.exhibitionId = exhibitionId;
		this.exhibitionName = exhibitionName;
		this.favoriteStatus = favoriteStatus;
		this.averageRatingScore = averageRatingScore;
		this.totalRatingCount = totalRatingCount;
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

	public boolean isFavoriteStatus() {
		return favoriteStatus;
	}

	public void setFavoriteStatus(boolean favoriteStatus) {
		this.favoriteStatus = favoriteStatus;
	}

	public Double getAverageRatingScore() {
		return averageRatingScore;
	}

	public void setAverageRatingScore(Double averageRatingScore) {
		this.averageRatingScore = averageRatingScore;
	}

	public Integer getTotalRatingCount() {
		return totalRatingCount;
	}

	public void setTotalRatingCount(Integer totalRatingCount) {
		this.totalRatingCount = totalRatingCount;
	}

}
