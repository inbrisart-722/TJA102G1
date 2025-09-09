package com.eventra.report.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name ="report")
public class ReportVO {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="report_id", insertable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reportId;
	
	@Column(name="report_status", insertable = false)
	private Integer reportStatus;
	
	@Column(name="member_id")
	private Integer memberId;
//	@ManyToOne
//	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
//	private MemberVO memberVO;
	
	@Column(name="comment_id")
	private Integer commentId;
//	@ManyToOne
//	@JoinColumn(name = "comment_id", referencedColumnName = "comment_id")
//	private CommentVO CommentVO;
	
	@Column(name="report_reason")
	private String reportReason;
	
	@Column(name="report_time", insertable = false, updatable = false)
	private Timestamp reportTime;

	public ReportVO() {
		super();
	}

	public ReportVO(Integer reportId, Integer reportStatus, Integer memberId, Integer commentId, String reportReason,
			Timestamp reportTime) {
		super();
		this.reportId = reportId;
		this.reportStatus = reportStatus;
		this.memberId = memberId;
		this.commentId = commentId;
		this.reportReason = reportReason;
		this.reportTime = reportTime;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public Integer getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(Integer reportStatus) {
		this.reportStatus = reportStatus;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getCommentId() {
		return commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}

	public String getReportReason() {
		return reportReason;
	}

	public void setReportReason(String reportReason) {
		this.reportReason = reportReason;
	}

	public Timestamp getReportTime() {
		return reportTime;
	}

	public void setReportTime(Timestamp reportTime) {
		this.reportTime = reportTime;
	}

}
