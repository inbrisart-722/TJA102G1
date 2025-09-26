package com.eventra.comment.model;

import java.util.List;
import java.util.Map;

public class LoadCommentResDTO {
	private String status;
	private List<CommentDTO> list;
	private boolean hasNextPage;
	private Map<Integer, String> mapReaction;
	
	public String getStatus() {
		return status;
	}
	public LoadCommentResDTO setStatus(String status) {
		this.status = status;
		return this;
	}
	public List<CommentDTO> getList() {
		return list;
	}
	public LoadCommentResDTO setList(List<CommentDTO> list) {
		this.list = list;
		return this;
	}
	public boolean isHasNextPage() {
		return hasNextPage;
	}
	public LoadCommentResDTO setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
		return this;
	}
	public Map<Integer, String> getMapReaction() {
		return mapReaction;
	}
	public LoadCommentResDTO setMapReaction(Map<Integer, String> mapReaction) {
		this.mapReaction = mapReaction;
		return this;
	}
}
