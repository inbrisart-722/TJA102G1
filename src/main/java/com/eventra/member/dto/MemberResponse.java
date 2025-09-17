package com.eventra.member.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MemberResponse {
	private Integer id;
	private String email;
	private String nickname;
	private String fullName;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	// getters/setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp timestamp) {
		this.createdAt = timestamp;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp timestamp) {
		this.updatedAt = timestamp;
	}

}
