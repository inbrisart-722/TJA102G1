package com.eventra.exhibitor.backend.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ExhibitorInfoUpdateDTO {

	@NotBlank(message = "公司/單位名稱為必填")
	@Size(max = 255, message = "名稱最多 255 字")
    private String companyName;
	
	@Size(max = 100, message = "名稱最多 100 字")
	private String exhibitorRegistrationName;

	@NotBlank(message = "聯絡電話必填")
	@Size(max = 50, message = "聯絡電話最多 50 字")
	@Pattern(regexp = "^09\\d{8}$", message = "請輸入 09 開頭的 10 碼手機")
	private String contactPhone;

	@NotBlank(message = "Email 必填")
	@Email(message = "Email 格式不正確")
	@Size(max = 100, message = "Email 最多 100 字")
	private String email;

	@Size(max = 255, message = "介紹最多 255 字")
	private String about;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

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
