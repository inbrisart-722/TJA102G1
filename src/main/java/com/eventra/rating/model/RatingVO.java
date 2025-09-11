package com.eventra.rating.model;

import java.sql.Timestamp;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.member.model.MemberVO;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "rating")
public class RatingVO {
	@Id
	@Column(name = "rating_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer ratingId;
	
//	@Column(name = "order_item_id", nullable = false)
//	private Integer orderItemId;
	
//	@Column(name = "exhibition_id", insertable = false, updatable = false)
//	private Integer exhibitionId;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exhibition_id", referencedColumnName = "exhibition_id", nullable = false)
	private ExhibitionVO exhibition;
	
//	@Column(name = "member_id", insertable = false, updatable = false)
//	private Integer memberId;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
	private MemberVO member;
	
	@Column(name ="rating_score", nullable = false)
	private Byte ratingScore;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
	
	public Integer getRatingId() {
		return ratingId;
	}
	public void setRatingId(Integer ratingId) {
		this.ratingId = ratingId;
	}
//	public Integer getOrderItemId() {
//		return orderItemId;
//	}
//	public void setOrderItemId(Integer orderItemId) {
//		this.orderItemId = orderItemId;
//	}
//	public Integer getExhibitionId() {
//		return exhibitionId;
//	}
//	public void setExhibitionId(Integer exhibitionId) {
//		this.exhibitionId = exhibitionId;
//	}
//	public Integer getMemberId() {
//		return memberId;
//	}
//	public void setMemberId(Integer memberId) {
//		this.memberId = memberId;
//	}
	public Byte getRatingScore() {
		return ratingScore;
	}
	public void setRatingScore(Byte ratingScore) {
		this.ratingScore = ratingScore;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public ExhibitionVO getExhibition() {
		return exhibition;
	}
	public void setExhibition(ExhibitionVO exhibition) {
		this.exhibition = exhibition;
//		this.exhibitionId = (exhibition != null ? exhibition.getExhibitionId() : null);
	}
	public MemberVO getMember() {
		return member;
	}
	public void setMember(MemberVO member) {
		this.member = member;
//		this.memberId = (member != null ? member.getMemberId() : null);
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
	
	public static class Builder{
		private ExhibitionVO exhibition;
		private MemberVO member;
		private Byte ratingScore;
		
		public Builder exhibition(ExhibitionVO exhibition){
			this.exhibition = exhibition;
			return this;
		}
		public Builder member(MemberVO member) {
			this.member = member;
			return this;
		}
		public Builder ratingScore(Byte ratingScore) {
			this.ratingScore = ratingScore;
			return this;
		}
		
		public RatingVO build() {
			RatingVO ratingVO = new RatingVO();
			ratingVO.setExhibition(exhibition);
			ratingVO.setMember(member);
			ratingVO.setRatingScore(ratingScore);
			return ratingVO;
		}
	}
}
//CREATE TABLE rating (
//	    rating_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '流水號PK',
//	    order_item_id INT NOT NULL COMMENT '訂單明細ID',
//	    exhibition_id INT NOT NULL COMMENT '展覽ID',
//	    member_id INT NOT NULL COMMENT '會員ID',
//	    rating_score TINYINT UNSIGNED NOT NULL COMMENT '評價分數',
//	    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '評價建立時間',
//	    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '評價最後編輯時間',
//
//	    CONSTRAINT fk_rating_orderitem
//	        FOREIGN KEY (order_item_id) REFERENCES order_item(order_item_id),
//	    CONSTRAINT fk_rating_exhibition
//	        FOREIGN KEY (exhibition_id) REFERENCES exhibition(exhibition_id),
//	    CONSTRAINT fk_rating_member
//	        FOREIGN KEY (member_id) REFERENCES member(member_id)
//	);