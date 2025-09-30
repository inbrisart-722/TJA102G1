package com.eventra.exhibitor.backend.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ExhibitorInfoUpdateDTO {

	@NotBlank(message = "名稱必填")
	@Size(max = 100, message = "名稱最多 100 字")
	private String exhibitorRegistrationName;

	@NotBlank(message = "聯絡電話必填")
	@Size(max = 50, message = "聯絡電話最多 50 字")
	// 手機 09xxxxxxxx 或市話(含 -) 的簡單規則；要更嚴格可再調整
	@Pattern(regexp = "^(09\\d{8}|0\\d{1,2}-?\\d{3,4}-?\\d{3,4})$", message = "請輸入正確的聯絡電話格式")
	private String contactPhone;

	@NotBlank(message = "Email 必填")
	@Email(message = "Email 格式不正確")
	@Size(max = 100, message = "Email 最多 100 字")
	private String email;

	@Size(max = 255, message = "介紹最多 255 字")
	private String about;

	public String getExhibitorRegistrationName() {
		return exhibitorRegistrationName;
	}

	public void setExhibitorRegistrationName(String exhibitorRegistrationName) {
		this.exhibitorRegistrationName = exhibitorRegistrationName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}
}
