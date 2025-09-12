package com.eventra.order.model;

import java.sql.Timestamp;

public class GetAllOrderResExhibitionDTO {
//	private String photoPortrait;
	private String exhibitionName;
	private String location;
	private Timestamp startTime;
	private Timestamp endTime;
	
	public String getExhibitionName() {
		return exhibitionName;
	}
	public GetAllOrderResExhibitionDTO setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
		return this;
	}
	public String getLocation() {
		return location;
	}
	public GetAllOrderResExhibitionDTO setLocation(String location) {
		this.location = location;
		return this;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public GetAllOrderResExhibitionDTO setStartTime(Timestamp startTime) {
		this.startTime = startTime;
		return this;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public GetAllOrderResExhibitionDTO setEndTime(Timestamp endTime) {
		this.endTime = endTime;
		return this;
	}
}
