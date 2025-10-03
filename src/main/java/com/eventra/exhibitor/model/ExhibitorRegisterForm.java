package com.eventra.exhibitor.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ExhibitorRegisterForm {

	@NotBlank(message = "統編欄位必填")
	@Pattern(regexp = "\\d{8}", message = "統編必須為8位數字")
	private String businessIdNumber;

	@NotBlank(message = "密碼欄位必填")
	@Pattern(
	    regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
	    message = "密碼需為至少 8 位，且至少含 1 個英文字母與 1 個數字"
	)
	private String password;

	@NotBlank(message = "確認密碼欄位必填")
	@Pattern(
	    regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
	    message = "確認密碼需為至少 8 位，且至少含 1 個英文字母與 1 個數字"
	)
	private String confirmPassword;


	@NotBlank(message = "公司名稱欄位必填")
	private String companyName;
	
	@NotBlank(message = "公司地址欄位必填")
	private String companyAddress;
	
	@NotBlank(message = "聯絡電話欄位必填")
	@Pattern(
		    regexp = "^(09\\d{2}-\\d{3}-\\d{3}|09\\d{8}|0\\d{1,3}-\\d{5,8})$",
		    message = "電話號碼格式不正確，請輸入手機（09xxxxxxxx 或 09xx-xxx-xxx）或市話（0x-xxxxxxx）"
		)
	private String contactPhone;
	
	@NotBlank(message = "聯絡信箱欄位必填")
	@Email(message = "聯絡信箱格式不正確") // 使用專門的 @Email 註解
	private String email;
	
	@NotBlank(message = "銀行代碼欄位必填")
	@Pattern(regexp = "^[0-9]{3}$", message = "銀行代碼應為 3 位數字")
	private String bankCode;
	
	@NotBlank(message = "銀行戶名欄位必填")
	private String bankAccountName;
	
	@NotBlank(message = "銀行帳號欄位必填")
	@Pattern(regexp = "^[0-9]{8,16}$", message = "銀行帳號應為8到16位數字")
	private String bankAccountNumber;

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getBusinessIdNumber() {
		return businessIdNumber;
	}

	public void setBusinessIdNumber(String businessIdNumber) {
		this.businessIdNumber = businessIdNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
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

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	
	
}
