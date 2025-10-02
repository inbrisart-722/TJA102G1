package com.eventra.eventnotification.dto;

import java.sql.Timestamp;

/**
 * API 查詢用, 帶完整資訊
 * 封裝後端要 回傳給 前端顯示所需的資訊
 * 
 * 與 NotificationMessageDTO 的差別
 * - EventNotificationDTO: 包含完整展覽與通知資訊, API 查詢用
 * - NotificationMessageDTO: 包含基本通知資訊. 傳輸用
 * 
 */

public class EventNotificationDTO {
	private Integer favoriteAnnouncementId;		// 通知ID
	private Integer exhibitionId;				// 展覽ID
	private String title;						// 標題
	private String content;						// 內容
	private Boolean readStatus;					// 是否已讀
	private Timestamp createdAt;				// 建立時間
	private String exhibitionName;				// 展覽名稱
	private String location;					// 展覽地點
	private String period; 						// 展覽期間

	// 無參數建構子
	public EventNotificationDTO() {
	}

	// 有參數建構子
	public EventNotificationDTO(Integer favoriteAnnouncementId, Integer exhibitionId, String title, String content, Boolean readStatus,
			Timestamp createdAt, String exhibitionName, String location, String period) {
		this.favoriteAnnouncementId = favoriteAnnouncementId;
		this.exhibitionId = exhibitionId;
		this.title = title;
		this.content = content;
		this.readStatus = readStatus;
		this.createdAt = createdAt;
		this.exhibitionName = exhibitionName;
		this.location = location;
		this.period = period;
	}

	// getter / setter
	public Integer getFavoriteAnnouncementId() {
		return favoriteAnnouncementId;
	}

	public void setFavoriteAnnouncementId(Integer favoriteAnnouncementId) {
		this.favoriteAnnouncementId = favoriteAnnouncementId;
	}

	public Integer getExhibitionId() {
		return exhibitionId;
	}

	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(Boolean readStatus) {
		this.readStatus = readStatus;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
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

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
}
