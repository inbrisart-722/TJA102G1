package com.eventra.exhibition.model;

public class ExhibitionReviewPageDTO {
	private String exhibitionStatus;
	private String exhibitionName;
	private String exhibitorName;
	private Integer exhibitionId;
	
	public String getExhibitionStatus() {
		return exhibitionStatus;
	}
	public ExhibitionReviewPageDTO setExhibitionStatus(String exhibitionStatus) {
		this.exhibitionStatus = exhibitionStatus;
		return this;
	}
	public String getExhibitionName() {
		return exhibitionName;
	}
	public ExhibitionReviewPageDTO setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
		return this;
	}
	public String getExhibitorName() {
		return exhibitorName;
	}
	public ExhibitionReviewPageDTO setExhibitorName(String exhibitorName) {
		this.exhibitorName = exhibitorName;
		return this;
	}
	public Integer getExhibitionId() {
		return exhibitionId;
	}
	public ExhibitionReviewPageDTO setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
		return this;
	}
}
