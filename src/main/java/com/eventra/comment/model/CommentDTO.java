package com.eventra.comment.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CommentDTO {
	private Member member;
	private Integer commentId;
	@JsonFormat(pattern = "yyyy-MM-dd") // 輸出只用到這格式
	private Timestamp createdAt;
	private String content;
	private Integer likeCount;
	private Integer dislikeCount;
	private Integer childCommentsCount;
	
	public Integer getCommentId() {
		return commentId;
	}
	public CommentDTO setCommentId(Integer commentId) {
		this.commentId = commentId;
		return this;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public CommentDTO setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
		return this;
	}
	public String getContent() {
		return content;
	}
	public CommentDTO setContent(String content) {
		this.content = content;
		return this;
	}
	public Integer getLikeCount() {
		return likeCount;
	}
	public CommentDTO setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
		return this;
	}
	public Integer getDislikeCount() {
		return dislikeCount;
	}
	public CommentDTO setDislikeCount(Integer dislikeCount) {
		this.dislikeCount = dislikeCount;
		return this;
	}
	public Integer getChildCommentsCount() {
		return childCommentsCount;
	}
	public CommentDTO setChildCommentsCount(Integer childCommentsCount) {
		this.childCommentsCount = childCommentsCount;
		return this;
	}
	public Member getMember() {
		return member;
	}
	public CommentDTO setMember(Member member) {
		this.member = member;
		return this;
	}


	public static class Member {
			private Integer memberId; // (暫無用）
			private String nickname;
			private String profilePic;
			public Integer getMemberId() {
				return memberId;
			}
			public Member setMemberId(Integer memberId) {
				this.memberId = memberId;
				return this;
			}
			public String getNickname() {
				return nickname;
			}
			public Member setNickname(String nickname) {
				this.nickname = nickname;
				return this;
			}
			public String getProfilePic() {
				return profilePic;
			}
			public Member setProfilePic(String profilePic) {
				this.profilePic = profilePic;
				return this;
			}
	}
}
