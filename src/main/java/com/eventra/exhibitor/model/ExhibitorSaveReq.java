package com.eventra.exhibitor.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ExhibitorSaveReq {

    

	private Integer exhibitorId;       // 有值 = 更新；無值 = 新增

    public Integer getExhibitorId() {
		return exhibitorId;
	}
	public void setExhibitorId(Integer exhibitorId) {
		this.exhibitorId = exhibitorId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
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
	public String getCompanyAddress() {
		return companyAddress;
	}
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}
	public Integer getReviewStatusId() {
		return reviewStatusId;
	}
	public void setReviewStatusId(Integer reviewStatusId) {
		this.reviewStatusId = reviewStatusId;
	}
	public String getRejectReason() {
		return rejectReason;
	}
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
	public String getGetExhibitorRegistrationName() {
		return getExhibitorRegistrationName;
	}
	public void setGetExhibitorRegistrationName(String getExhibitorRegistrationName) {
		this.getExhibitorRegistrationName = getExhibitorRegistrationName;
	}
	@NotBlank
    private String companyName;

    @Pattern(regexp = "\\d{8}", message = "統編需 8 位數字")
    private String businessIdNumber;

    @Email
    private String email;

    private String contactName;
    private String contactPhone;
    private String companyAddress;
    private String getExhibitorRegistrationName;
    // 審核相關（成功/失敗按鈕可以直接設這個）
    private Integer reviewStatusId;    // 1=PENDING, 2=APPROVED, 3=REJECTED（範例）
    private String rejectReason;       // 若是失敗才會用到
}
