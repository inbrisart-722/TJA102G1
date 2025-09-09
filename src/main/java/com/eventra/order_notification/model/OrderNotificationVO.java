package com.eventra.order_notification.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table (name ="order_notification")
public class OrderNotificationVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="order_announcement_id" , insertable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderAnnouncementId;
	
	@Column(name ="member_id")
	private Integer memberId;
//	@ManyToOne
//	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
//	private MemberVO member;
	
	@Column(name ="order_id")
	private Integer orderId;
//	@ManyToOne
//	@JoinColumn(name = "order_id", referencedColumnName = "order_id")
//	private OrderVO order;
	
	@Column(name ="read_status", nullable = false)
	private Boolean readStatus = false; 
	
	private String title;
	
	private String content;
	
	@Column(name ="created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	// 無參數建構子
	public OrderNotificationVO() {
		super();
	}

	// 有參數建構子
	public OrderNotificationVO(Integer orderAnnouncementId, Integer memberId, Integer orderId, Boolean readStatus,
			String title, String content, Timestamp createdAt) {
		super();
		this.orderAnnouncementId = orderAnnouncementId;
		this.memberId = memberId;
		this.orderId = orderId;
		this.readStatus = readStatus;
		this.title = title;
		this.content = content;
		this.createdAt = createdAt;
	}

	// getter/setter
	public Integer getOrderAnnouncementId() {
		return orderAnnouncementId;
	}

	public void setOrderAnnouncementId(Integer orderAnnouncementId) {
		this.orderAnnouncementId = orderAnnouncementId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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
