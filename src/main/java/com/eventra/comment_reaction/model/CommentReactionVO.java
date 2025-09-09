package com.eventra.comment_reaction.model;

import java.sql.Timestamp;

import com.eventra.comment.model.CommentVO;
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
@Table(name = "comment_reaction")
public class CommentReactionVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_reaction_id")
	private Integer commentReactionId;

//	@Column(name = "comment_id", insertable = false, updatable = false)
//	private Integer commentId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", referencedColumnName = "comment_id", nullable = false)
	private CommentVO comment;

//	@Column(name = "member_id", insertable = false, updatable = false)
//	private Integer memberId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
	private MemberVO member;

	@Column(name = "reaction", nullable = false)
	private String reaction;

	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

	public Integer getCommentReactionId() {
		return commentReactionId;
	}

	public void setCommentReactionId(Integer commentReactionId) {
		this.commentReactionId = commentReactionId;
	}

//	public Integer getCommentId() {
//		return commentId;
//	}
//	public void setCommentId(Integer commentId) {
//		this.commentId = commentId;
//	}
//	public Integer getMemberId() {
//		return memberId;
//	}
//	public void setMemberId(Integer memberId) {
//		this.memberId = memberId;
//	}
	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
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

	public CommentVO getComment() {
		return comment;
	}

	public void setComment(CommentVO comment) {
		this.comment = comment;
//		this.commentId = (comment != null ? comment.getCommentId() : null); // 物件層同步（DB 寫入仍由關聯主導）
	}

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
//		this.memberId = (member != null ? member.getMemberId() : null); // 物件層同步（DB 寫入仍由關聯主導）
	}

	public static class Builder {
		private CommentVO comment;
		private MemberVO member;
		private String reaction;

		public Builder comment(CommentVO comment) {
			this.comment = comment;
			return this;
		}
		public Builder member(MemberVO member) {
			this.member = member;
			return this;
		}
		public Builder reaction(String reaction) {
			this.reaction = reaction;
			return this;
		}
		public CommentReactionVO build() {
			CommentReactionVO vo = new CommentReactionVO();
			vo.setComment(this.comment);
			vo.setMember(this.member);
			vo.setReaction(this.reaction);
			return vo;
		}
	}
}
