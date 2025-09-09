package com.eventra.exhibitor_review_log.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "exhibitor_review_log")
public class ExhibitorReviewLogVO {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "exhibitor_review_id", insertable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer exhibitorReviewId;

	@Column(name = "exhibitor_id")
	private Integer exhibitorId;

	@Column(name = "reject_reason", nullable = true)
	private String rejectReason;

	@Column(name = "reviewed_at", insertable = false, updatable = false)
	private Timestamp reviewedAt;

	public ExhibitorReviewLogVO() {
		super();
	}

	public ExhibitorReviewLogVO(Integer exhibitorReviewId, Integer exhibitorId, String rejectReason,
			Timestamp reviewedAt) {
		super();
		this.exhibitorReviewId = exhibitorReviewId;
		this.exhibitorId = exhibitorId;
		this.rejectReason = rejectReason;
		this.reviewedAt = reviewedAt;
	}

	public Integer getExhibitorReviewId() {
		return exhibitorReviewId;
	}

	public void setExhibitorReviewId(Integer exhibitorReviewId) {
		this.exhibitorReviewId = exhibitorReviewId;
	}

	public Integer getExhibitorId() {
		return exhibitorId;
	}

	public void setExhibitorId(Integer exhibitorId) {
		this.exhibitorId = exhibitorId;
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
