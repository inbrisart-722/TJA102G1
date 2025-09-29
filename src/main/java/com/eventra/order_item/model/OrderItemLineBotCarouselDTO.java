package com.eventra.order_item.model;

import java.time.LocalDateTime;

public class OrderItemLineBotCarouselDTO {
	private String photoPortrait;
	private String pageUrl; // ".../front-end/exhibitions?exhibitionId=" + exhibitionId 即可
	private String exhibitionName;
	private String orderItemUlid; // 訂單明細編號，留檔用
	private String ticketCode; // qr code 抓這個
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String location;
	
	
	public String getPageUrl() {
		return pageUrl;
	}
	public OrderItemLineBotCarouselDTO setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
		return this;
	}
	public String getTicketCode() {
		return ticketCode;
	}
	public OrderItemLineBotCarouselDTO setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
		return this;
	}
	public String getPhotoPortrait() {
		return photoPortrait;
	}
	public OrderItemLineBotCarouselDTO setPhotoPortrait(String photoPortrait) {
		this.photoPortrait = photoPortrait;
		return this;
	}
	public String getExhibitionName() {
		return exhibitionName;
	}
	public OrderItemLineBotCarouselDTO setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
		return this;
	}
	public String getOrderItemUlid() {
		return orderItemUlid;
	}
	public OrderItemLineBotCarouselDTO setOrderItemUlid(String orderItemUlid) {
		this.orderItemUlid = orderItemUlid;
		return this;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public OrderItemLineBotCarouselDTO setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
		return this;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public OrderItemLineBotCarouselDTO setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
		return this;
	}
	public String getLocation() {
		return location;
	}
	public OrderItemLineBotCarouselDTO setLocation(String location) {
		this.location = location;
		return this;
	}
	
	
}
