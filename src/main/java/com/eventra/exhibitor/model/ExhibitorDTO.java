package com.eventra.exhibitor.model;

public class ExhibitorDTO {
	private Integer exhibitorId;
	private String exhibitorDisplayName;
	// exhibitorRegistration != null 
		// ? exhibitorRegistrationName
		// : companyName
	public Integer getExhibitorId() {
		return exhibitorId;
	}
	public ExhibitorDTO setExhibitorId(Integer exhibitorId) {
		this.exhibitorId = exhibitorId;
		return this;
	}
	public String getExhibitorDisplayName() {
		return exhibitorDisplayName;
	}
	public ExhibitorDTO setExhibitorDisplayName(String exhibitorDisplayName) {
		this.exhibitorDisplayName = exhibitorDisplayName;
		return this;
	}
	
}
