package com.eventra.exhibitor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "exhibitor")
public class ExhibitorVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exhibitor_id")
	private Integer exhibitorId;


    @Column(name = "review_status_id")
	private Integer reviewStatusId;
	

	private String exhibitorRegistrationName;

    @NotBlank(message = "統編欄位必填")
    @Pattern(regexp="\\d{8}", message = "統編必須為8位數字")
	@Column(name = "business_id_number", nullable = false, length = 8)
	private String businessIdNumber;

    @NotBlank(message = "密碼欄位必填")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
//            message = "密碼需為至少 8 位，且至少含 1 個英文字母與 1 個數字")
	@Column(name = "password_hash")
	private String passwordHash;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "contact_name")
	private String contactName;
	
	@Column(name = "contact_phone")
	private String contactPhone;
	
	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "company_address")
	private String companyAddress;
	
	@Column(name = "bank_account_name")
	private String bankAccountName;
	
	@Column(name = "bank_code")
	private String bankCode;
	
	@Column(name = "bank_account_number")
	private String bankAccountNumber;
	
	@Column(name = "website_url")
	private String websiteUrl;
	
	@Column(name = "about")
	private String about;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public ExhibitorVO() {
		super();
	}

	public ExhibitorVO(Integer exhibitorId, Integer reviewStatusId, String exhibitorRegistrationName,
			String businessIdNumber, String passwordHash, String email, String contactName, String contactPhone,
			String companyName, String companyAddress, String bankAccountName, String bankCode,
			String bankAccountNumber, String websiteUrl, String about, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.exhibitorId = exhibitorId;
		this.reviewStatusId = reviewStatusId;
		this.exhibitorRegistrationName = exhibitorRegistrationName;
		this.businessIdNumber = businessIdNumber;
		this.passwordHash = passwordHash;
		this.email = email;
		this.contactName = contactName;
		this.contactPhone = contactPhone;
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.bankAccountName = bankAccountName;
		this.bankCode = bankCode;
		this.bankAccountNumber = bankAccountNumber;
		this.websiteUrl = websiteUrl;
		this.about = about;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Integer getExhibitorId() {
		return exhibitorId;
	}

	public void setExhibitorId(Integer exhibitorId) {
		this.exhibitorId = exhibitorId;
	}

	public Integer getReviewStatusId() {
		return reviewStatusId;
	}

	public void setReviewStatusId(Integer reviewStatusId) {
		this.reviewStatusId = reviewStatusId;
	}

	public String getExhibitorRegistrationName() {
		return exhibitorRegistrationName;
	}

	public void setExhibitorRegistrationName(String exhibitorRegistrationName) {
		this.exhibitorRegistrationName = exhibitorRegistrationName;
	}

	public String getBusinessIdNumber() {
		return businessIdNumber;
	}

	public void setBusinessIdNumber(String businessIdNumber) {
		this.businessIdNumber = businessIdNumber;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
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

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
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

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
}
