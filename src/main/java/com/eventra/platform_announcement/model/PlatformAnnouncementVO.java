package com.eventra.platform_announcement.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name ="platform_announcement")
public class PlatformAnnouncementVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "announcement_id", insertable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer platformAnnouncementId;
	
	private String title;
	
	@Column(name ="content", columnDefinition = "longtext") // 定義資料庫的型別為longtext
	private String content;
	
	@Column(name ="created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name ="updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
	
	// 無參數建構子
	public PlatformAnnouncementVO() {
		super();
	}

	// 有參數建構子
	public PlatformAnnouncementVO(Integer platformAnnouncementId, String title, String content, Timestamp createdAt,
			Timestamp updatedAt) {
		super();
		this.platformAnnouncementId = platformAnnouncementId;
		this.title = title;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	//	新增用
	public PlatformAnnouncementVO(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}

	// getter/setter
	public Integer getPlatformAnnouncementId() {
		return platformAnnouncementId;
	}

	public void setPlatformAnnouncementId(Integer platformAnnouncementId) {
		this.platformAnnouncementId = platformAnnouncementId;
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

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
