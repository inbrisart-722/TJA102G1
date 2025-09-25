package com.eventra.eventnotification.dto;

import java.sql.Timestamp;

public class EventNotificationDTO {
	private Integer favoriteAnnouncementId;
	private Integer exhibitionId;
	private String title;
	private String content;
	private Boolean readStatus;
	private Timestamp createdAt;

	// 額外展覽資訊（選填）
	private String exhibitionName;
	private String location;
	private String period; // start_time ~ end_time

	public EventNotificationDTO() {
	}

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

	// getter/setter
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
