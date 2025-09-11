package com.eventra.favorite.dto;

public class FavoriteToggleDTO {
    private Integer exhibitionId;
    private boolean favoriteStatus;
    private boolean success;     // 確認是否成功
    
    // 無參數建構子
	public FavoriteToggleDTO() {
		super();
	}

	// 有參數建構子
	public FavoriteToggleDTO(Integer exhibitionId, boolean favoriteStatus, boolean success) {
		super();
		this.exhibitionId = exhibitionId;
		this.favoriteStatus = favoriteStatus;
		this.success = success;
	}

	// getter/setter
	public Integer getExhibitionId() {
		return exhibitionId;
	}

	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}

	public boolean isFavoriteStatus() {
		return favoriteStatus;
	}

	public void setFavoriteStatus(boolean favoriteStatus) {
		this.favoriteStatus = favoriteStatus;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
    
}
