package com.eventra.eventnotification.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.eventra.favorite.model.FavoriteVO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table (name ="event_notification")
public class EventNotificationVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="favorite_announcement_id" , insertable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer favoriteAnnouncementId;
	
	@ManyToOne
	@JoinColumn(name = "favorite_id", referencedColumnName = "favorite_id")
	@JsonIgnore
	private FavoriteVO favoriteVO;
	
	@Column(name ="member_id")
	private Integer memberId;
	
	@Column(name ="read_status", nullable = false)
	private Boolean readStatus = false; // default false
	
	private String title;
	
	private String content;
	
	@Column(name ="created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	// 無參數建構子
	public EventNotificationVO() {
		super();
	}

	// 有參數建構子
	public EventNotificationVO(Integer favoriteAnnouncementId, FavoriteVO favoriteVO, Integer memberId,
			Boolean readStatus, String title, String content, Timestamp createdAt) {
		super();
		this.favoriteAnnouncementId = favoriteAnnouncementId;
		this.favoriteVO = favoriteVO;
		this.memberId = memberId;
		this.readStatus = readStatus;
		this.title = title;
		this.content = content;
		this.createdAt = createdAt;
	}
	
	// getter/setter
	public Integer getFavoriteAnnouncementId() {
		return favoriteAnnouncementId;
	}

	public void setFavoriteAnnouncementId(Integer favoriteAnnouncementId) {
		this.favoriteAnnouncementId = favoriteAnnouncementId;
	}

	public FavoriteVO getFavoriteVO() {
		return favoriteVO;
	}

	public void setFavoriteVO(FavoriteVO favoriteVO) {
		this.favoriteVO = favoriteVO;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Boolean getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(Boolean readStatus) {
		this.readStatus = readStatus;
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

}
