package com.eventra.eventnotification.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.eventra.exhibition.model.ExhibitionVO;

import jakarta.persistence.*;

@Entity
@Table(name = "event_notification")
public class EventNotificationVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "favorite_announcement_id", insertable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer favoriteAnnouncementId;

	@Column(name = "favorite_id", nullable = false)
	private Integer favoriteId;

	@Column(name = "member_id", nullable = false)
	private Integer memberId;

	@Column(name = "exhibition_id", nullable = false)
	private Integer exhibitionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exhibition_id", referencedColumnName = "exhibition_id", insertable = false, updatable = false)
	private ExhibitionVO exhibition;

	@Column(name = "notification_type", nullable = false)
	private String notificationType; 	// OPENING_SOON / LOW_STOCK / TIME_CHANGE / LOCATION_CHANGE

	@Column(name = "read_status", nullable = false)
	private Boolean readStatus = false; // default false

	@Column(name = "threshold")
	private Integer threshold; 			// 低庫存門檻 (只適用於 LOW_STOCK 類型, 其餘可為 null)

	private String title;

	private String content;

	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	// 無參數建構子
	public EventNotificationVO() {
	}

	// 有參數建構子
	public EventNotificationVO(Integer favoriteAnnouncementId, Integer favoriteId, Integer memberId,
			Integer exhibitionId, ExhibitionVO exhibition, String notificationType, Boolean readStatus,
			Integer threshold, String title, String content, Timestamp createdAt) {
		super();
		this.favoriteAnnouncementId = favoriteAnnouncementId;
		this.favoriteId = favoriteId;
		this.memberId = memberId;
		this.exhibitionId = exhibitionId;
		this.exhibition = exhibition;
		this.notificationType = notificationType;
		this.readStatus = readStatus;
		this.threshold = threshold;
		this.title = title;
		this.content = content;
		this.createdAt = createdAt;
	}
	
	// getter / setter
	public Integer getFavoriteAnnouncementId() {
		return favoriteAnnouncementId;
	}

	public void setFavoriteAnnouncementId(Integer favoriteAnnouncementId) {
		this.favoriteAnnouncementId = favoriteAnnouncementId;
	}

	public Integer getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(Integer favoriteId) {
		this.favoriteId = favoriteId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getExhibitionId() {
		return exhibitionId;
	}

	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}

	public ExhibitionVO getExhibition() {
		return exhibition;
	}

	public void setExhibition(ExhibitionVO exhibition) {
		this.exhibition = exhibition;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public Boolean getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(Boolean readStatus) {
		this.readStatus = readStatus;
	}

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
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
