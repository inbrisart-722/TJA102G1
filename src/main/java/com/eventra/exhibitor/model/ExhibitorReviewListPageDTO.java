package com.eventra.exhibitor.model;

public class ExhibitorReviewListPageDTO {
	private Integer exhibitorId;
	private String exhibitorReviewStatus;
	private String companyName;
	private String businessIdNumber;
	private String email;
	private String contactPhone;
	
	public Integer getExhibitorId() {
		return exhibitorId;
	}
	public void setExhibitorId(Integer exhibitorId) {
		this.exhibitorId = exhibitorId;
	}
	public String getExhibitorReviewStatus() {
		return exhibitorReviewStatus;
	}
	public void setExhibitorReviewStatus(String exhibitorReviewStatus) {
		this.exhibitorReviewStatus = exhibitorReviewStatus;
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
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
}
