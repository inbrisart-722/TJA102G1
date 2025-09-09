package com.eventra.favorite.model;

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
@Table (name ="favorite")
public class FavoriteVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "favorite_id", insertable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer favoriteId;

	@Column(name ="member_id")
	private Integer memberId;
//	@ManyToOne
//	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
//	private MemberVO member;
	
	@Column(name ="exhibition_id")
	private Integer exhibitionId;
//	@ManyToOne
//	@JoinColumn(name = "exhibition_id", referencedColumnName = "exhibition_id")
//	private ExhibitionVO exhibition;
	
	@Column(name ="favorite_status", insertable = false)
	private Integer favoriteStatus;

	@Column(name ="created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name ="updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

	
	// 無參數建構子
	public FavoriteVO() {
		super();
	}
	
	// 有參數建構子
	public FavoriteVO(Integer favoriteId, Integer memberId, Integer exhibitionId, Integer favoriteStatus,
			Timestamp createdAt, Timestamp updatedAt) {
		super();
		this.favoriteId = favoriteId;
		this.memberId = memberId;
		this.exhibitionId = exhibitionId;
		this.favoriteStatus = favoriteStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// getter/setter
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

	public Integer getFavoriteStatus() {
		return favoriteStatus;
	}

	public void setFavoriteStatus(Integer favoriteStatus) {
		this.favoriteStatus = favoriteStatus;
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
