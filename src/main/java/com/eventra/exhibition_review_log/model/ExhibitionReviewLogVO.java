package com.eventra.exhibition_review_log.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name ="exhibition_review_log")
public class ExhibitionReviewLogVO {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="exhibition_review_id" , insertable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer exhibitionReviewId;
	
	@Column(name="exhibition_id")
	private Integer exhibitionId;
	
	@Column(name="reject_reason", nullable = true)
	private String rejectReason;
	
	@Column(name ="reviewed_at", insertable = false, updatable = false)
	private Timestamp reviewedAt;

	public ExhibitionReviewLogVO() {
		super();
	}

	public ExhibitionReviewLogVO(Integer exhibitionReviewId, Integer exhibitionId, String rejectReason,
			Timestamp reviewedAt) {
		super();
		this.exhibitionReviewId = exhibitionReviewId;
		this.exhibitionId = exhibitionId;
		this.rejectReason = rejectReason;
		this.reviewedAt = reviewedAt;
	}

	public Integer getExhibitionReviewId() {
		return exhibitionReviewId;
	}

	public void setExhibitionReviewId(Integer exhibitionReviewId) {
		this.exhibitionReviewId = exhibitionReviewId;
	}

	public Integer getExhibitionId() {
		return exhibitionId;
	}

	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public Timestamp getReviewedAt() {
		return reviewedAt;
	}

	public void setReviewedAt(Timestamp reviewedAt) {
		this.reviewedAt = reviewedAt;
	}
	
}
