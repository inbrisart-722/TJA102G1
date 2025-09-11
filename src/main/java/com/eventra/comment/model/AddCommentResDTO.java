package com.eventra.comment.model;

public class AddCommentResDTO {
	private String status;
	private CommentVO commentVO;
	private Integer commentCount;
	private Integer replyCount; // 可代表 childCount 或 exhibitionCommentCount
	
	public String getStatus() {
		return status;
	}
	public AddCommentResDTO setStatus(String status) {
		this.status = status;
		return this;
	}
	public CommentVO getCommentVO() {
		return commentVO;
	}
	public AddCommentResDTO setCommentVO(CommentVO commentVO) {
		this.commentVO = commentVO;
		return this;
	}
	public Integer getCommentCount() {
		return commentCount;
	}
	public AddCommentResDTO setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
		return this;
	}
	public Integer getReplyCount() {
		return replyCount;
	}
	public AddCommentResDTO setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
		return this;
	}

}
