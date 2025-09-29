package com.eventra.order.model;

public class OrderLineBotCarouselDTO {
//	private String photoPortrait;
//	private String exhibitionName;
	private OrderStatus orderStatus;
	private Integer totalAmount;
	private Integer totalQuantity;
	private String orderUlid;
	
//	public String getPhotoPortrait() {
//		return photoPortrait;
//	}
//	public OrderLineBotCarouselDTO setPhotoPortrait(String photoPortrait) {
//		this.photoPortrait = photoPortrait;
//		return this;
//	}
//	public String getExhibitionName() {
//		return exhibitionName;
//	}
//	public OrderLineBotCarouselDTO setExhibitionName(String exhibitionName) {
//		this.exhibitionName = exhibitionName;
//		return this;
//	}
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	public OrderLineBotCarouselDTO setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
		return this;
	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public OrderLineBotCarouselDTO setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}
	public Integer getTotalQuantity() {
		return totalQuantity;
	}
	public OrderLineBotCarouselDTO setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
		return this;
	}
	public String getOrderUlid() {
		return orderUlid;
	}
	public OrderLineBotCarouselDTO setOrderUlid(String orderUlid) {
		this.orderUlid = orderUlid;
		return this;
	}
}
