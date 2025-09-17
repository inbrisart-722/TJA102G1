package com.eventra.order.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class GetAllOrderResExhibitionDTO {
//	private String photoPortrait;
	private String exhibitionName;
	private String location;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	
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
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public GetAllOrderResExhibitionDTO setStartTime (LocalDateTime startTime) {
		this.startTime = startTime;
		return this;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public GetAllOrderResExhibitionDTO setEndTime (LocalDateTime endTime) {
		this.endTime = endTime;
		return this;
	}
}
