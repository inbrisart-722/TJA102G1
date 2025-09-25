package com.eventra.favorite.dto;

public class FavoriteDTO {
    private Integer exhibitionId;		// 展覽id
    private String exhibitionName;  	// 展覽名稱
    private boolean favoriteStatus;		// 收藏狀態
    
    // 無參數建構子
	public FavoriteDTO() {
		super();
	}

	// 有參數建構子
    public FavoriteDTO(Integer exhibitionId, String exhibitionName, boolean favoriteStatus) {
        this.exhibitionId = exhibitionId;
        this.exhibitionName = exhibitionName;
        this.favoriteStatus = favoriteStatus;
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
    
}
