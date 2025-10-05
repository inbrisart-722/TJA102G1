package com.eventra.exhibition.backend.controller.dto;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.*;

@JsonIgnoreProperties(ignoreUnknown = true) // 多傳欄位直接忽略
@JsonInclude(JsonInclude.Include.NON_NULL) // 回傳時省略 null 欄位（若你要回 DTO）
public class ExhibitionReviewReqDTO {

	// 必填
	@NotNull(message = "exhibitionId")
	private Integer exhibitionId;

	// 3=審核失敗, 4=審核成功, 5=已結束（若你要更嚴謹可改 enum）
	@NotNull(message = "statusId")
	private Integer statusId;

	// 僅 statusId=3 需要；用自訂驗證在 controller/service 檢查
	private String rejectReason;

	// ExhibitionVO 欄位（可為 null 代表不更新）
	private String exhibitionName;
	private String description;

	// ExhibitorVO 欄位（可為 null 代表不更新）
	private String companyName;
	private String businessIdNumber;
	private String contactPhone;
	private String contactName;
	private String companyAddress;

	@Email(message = "email 格式不正確")
	private String email;

	/* ===== Getter / Setter ===== */
	public Integer getExhibitionId() {
		return exhibitionId;
	}

	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getExhibitionName() {
		return exhibitionName;
	}

	public void setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
	
}
