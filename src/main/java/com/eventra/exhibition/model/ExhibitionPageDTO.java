package com.eventra.exhibition.model;

import java.time.LocalDateTime;
import java.util.Map;

import com.eventra.exhibitor.model.ExhibitorDTO;

// inbrisart 20250925 給展覽頁 SSR 帶入
public class ExhibitionPageDTO {
	// 頂層
	private String photoLandscape;
	private Integer exhibitionId; // 珮甄會用到 body dataset attribute
	private String exhibitionName;
	private Double averageRatingScore;
	private Integer totalRatingCount;
	private Integer leftTicketQuantity;
	private Integer cheapestTicketPrice;
	// ticketTypeName -> price
	private Map<Integer, Integer> tickets;
	private Boolean isTicketStart; 
	private Boolean isExhibitionEnded;
	private LocalDateTime ticketStartTime;
	// 展覽資訊區
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String location;
	private String description;
	private ExhibitorDTO exhibitor; // 帶入 id 後拼接展商主頁網址
	private Integer totalCommentCount;

	
	public Boolean getIsExhibitionEnded() {
		return isExhibitionEnded;
	}
	public void setIsExhibitionEnded(Boolean isExhibitionEnded) {
		this.isExhibitionEnded = isExhibitionEnded;
	}
	public Boolean getIsTicketStart() {
		return isTicketStart;
	}
	public void setIsTicketStart(Boolean isTicketStart) {
		this.isTicketStart = isTicketStart;
	}
	public LocalDateTime getTicketStartTime() {
		return ticketStartTime;
	}
	public void setTicketStartTime(LocalDateTime ticketStartTime) {
		this.ticketStartTime = ticketStartTime;
	}
	public String getPhotoLandscape() {
		return photoLandscape;
	}
	public void setPhotoLandscape(String photoLandscape) {
		this.photoLandscape = photoLandscape;
	}
	public Integer getExhibitionId() {
		return exhibitionId;
	}
	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}
	public String getExhibitionName() {
		return exhibitionName;
	}
	public void setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
	}
	public Double getAverageRatingScore() {
		return averageRatingScore;
	}
	public void setAverageRatingScore(Double averageRatingScore) {
		this.averageRatingScore = averageRatingScore;
	}
	public Integer getTotalRatingCount() {
		return totalRatingCount;
	}
	public void setTotalRatingCount(Integer totalRatingCount) {
		this.totalRatingCount = totalRatingCount;
	}
	public Integer getLeftTicketQuantity() {
		return leftTicketQuantity;
	}
	public void setLeftTicketQuantity(Integer leftTicketQuantity) {
		this.leftTicketQuantity = leftTicketQuantity;
	}
	public Map<Integer, Integer> getTickets() {
		return tickets;
	}
	public void setTickets(Map<Integer, Integer> tickets) {
		this.tickets = tickets;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ExhibitorDTO getExhibitor() {
		return exhibitor;
	}
	public void setExhibitor(ExhibitorDTO exhibitor) {
		this.exhibitor = exhibitor;
	}
	public Integer getCheapestTicketPrice() {
		return cheapestTicketPrice;
	}
	public void setCheapestTicketPrice(Integer cheapestTicketPrice) {
		this.cheapestTicketPrice = cheapestTicketPrice;
	}
	public Integer getTotalCommentCount() {
		return totalCommentCount;
	}
	public void setTotalCommentCount(Integer totalCommentCount) {
		this.totalCommentCount = totalCommentCount;
	}
}
