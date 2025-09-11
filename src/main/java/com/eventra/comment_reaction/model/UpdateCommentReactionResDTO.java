package com.eventra.comment_reaction.model;

public class UpdateCommentReactionResDTO {
	private String status;
	private String currentReaction;
	private Integer likeCount;
	private Integer dislikeCount;
	
	public String getStatus() {
		return status;
	}
	public UpdateCommentReactionResDTO setStatus(String status) {
		this.status = status;
		return this;
	}
	public String getCurrentReaction() {
		return currentReaction;
	}
	public UpdateCommentReactionResDTO setCurrentReaction(String currentReaction) {
		this.currentReaction = currentReaction;
		return this;
	}
	
	public Integer getLikeCount() {
		return likeCount;
	}
	public UpdateCommentReactionResDTO setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
		return this;
	}
	public Integer getDislikeCount() {
		return dislikeCount;
	}
	public UpdateCommentReactionResDTO setDislikeCount(Integer dislikeCount) {
		this.dislikeCount = dislikeCount;
		return this;
	}
	
}
