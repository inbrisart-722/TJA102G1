package com.eventra.member.verif.model;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.RequestPart;

public class UpdateInfoReqDTO {
	private String nickname;
	private String phoneNumber;
	private LocalDate birthDate;
	private String address;
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public LocalDate getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	// Spring Boot 2.x/3.x 內建支援 LocalDate 轉換（透過 ConversionService）。
	// 前端傳過來的格式必須是 yyyy-MM-dd，否則會轉換失敗。

	
}
