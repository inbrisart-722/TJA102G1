package com.eventra.favorite.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.member.model.MemberVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer favoriteId;

    @Column(name ="member_id")
    private Integer memberId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", insertable=false, updatable=false)
    private MemberVO member;
    
    @Column(name ="exhibition_id")
    private Integer exhibitionId; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_id", referencedColumnName = "exhibition_id", insertable=false, updatable=false)
    private ExhibitionVO exhibition;
    
    @Column(name ="favorite_status")
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

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
	}

	public ExhibitionVO getExhibition() {
		return exhibition;
	}

	public void setExhibition(ExhibitionVO exhibition) {
		this.exhibition = exhibition;
	}

}
