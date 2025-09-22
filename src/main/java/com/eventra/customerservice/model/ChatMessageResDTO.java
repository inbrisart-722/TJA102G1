package com.eventra.customerservice.model;

public class ChatMessageResDTO {

	private Integer memberId;
	private Integer agentId;
	private String avatarSrc;
	private String content;
	private Long sentTime;
	
	public Integer getMemberId() {
		return memberId;
	}
	public ChatMessageResDTO setMemberId(Integer memberId) {
		this.memberId = memberId;
		return this;
	}
	public Integer getAgentId() {
		return agentId;
	}
	public ChatMessageResDTO setAgentId(Integer agentId) {
		this.agentId = agentId;
		return this;
	}
	public String getAvatarSrc() {
		return avatarSrc;
	}
	public ChatMessageResDTO setAvatarSrc(String avatarSrc) {
		this.avatarSrc = avatarSrc;
		return this;
	}
	public String getContent() {
		return content;
	}
	public ChatMessageResDTO setContent(String content) {
		this.content = content;
		return this;
	}
	public Long getSentTime() {
		return sentTime;
	}
	public ChatMessageResDTO setSentTime(Long sentTime) {
		this.sentTime = sentTime;
		return this;
	}
	
}
