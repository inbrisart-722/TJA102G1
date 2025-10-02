package com.eventra.exhibitor.backend.controller.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ExhibitorAccountUpdateDTO {

	@NotBlank(message="統編為必填")
    @Pattern(regexp="^\\d{8}$", message="統編須為 8 碼數字")
    private String businessIdNumber;

    @NotBlank(message="Email 為必填")
    @Email(message="Email 格式不正確")
    @Size(max=100, message="Email 最長 100 字")
    private String email;

    @Size(max=100, message="聯絡人姓名最長 100 字")
    private String contactName; // 可為空

    @NotBlank(message="聯絡電話為必填")
    @Size(max=50, message="聯絡電話最長 50 字")
    private String contactPhone;

    @NotBlank(message="公司/單位名稱為必填")
    @Size(max=255, message="公司/單位名稱最長 255 字")
    private String companyName;

    // 若此頁不編輯地址，可省略；若要編輯就加上：
    // @NotBlank(message="公司地址為必填")
    // @Size(max=255, message="公司地址最長 255 字")
    // private String companyAddress;

    @Size(max=100, message="平台註冊名稱最長 100 字")
    private String exhibitorRegistrationName; // 可為空

    @NotBlank(message="銀行戶名為必填")
    @Size(max=100, message="銀行戶名最長 100 字")
    private String bankAccountName;

    @NotBlank(message="銀行代碼為必填")
    @Pattern(regexp="^\\d{3}$", message="銀行代碼須為 3 碼數字")
    private String bankCode;

    @NotBlank(message="銀行帳號為必填")
    @Pattern(regexp="^\\d{7,30}$", message="銀行帳號須為 7–30 碼數字")
    private String bankAccountNumber;

	public String getBusinessIdNumber() {
		return businessIdNumber;
	}

	public void setBusinessIdNumber(String businessIdNumber) {
		this.businessIdNumber = businessIdNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

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

	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

}
