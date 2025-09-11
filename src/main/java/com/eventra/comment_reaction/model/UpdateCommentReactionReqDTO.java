package com.eventra.comment_reaction.model;

public class UpdateCommentReactionReqDTO {
	private Integer commentId;
	private String reaction;
	
	public Integer getCommentId() {
		return commentId;
	}
	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}
	public String getReaction() {
		return reaction;
	}
	public void setReaction(String reaction) {
		this.reaction = reaction;
	}
}
