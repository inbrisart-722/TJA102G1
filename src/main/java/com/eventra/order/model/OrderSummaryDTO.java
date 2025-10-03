package com.eventra.order.model;

import java.sql.Timestamp;

import com.eventra.member.model.MemberVO;

public class OrderSummaryDTO {

	private Integer orderId;
	private String orderUlid;
	private String fullName;
	private OrderStatus orderStatus;
	private Long itemCount;
	private Integer totalAmount;
	private Timestamp createdAt;
	private String exhibitionName;
	private String ticketTypeName;
	
	public OrderSummaryDTO(Integer orderId, String orderUlid, String fullName, OrderStatus orderStatus,
			Long itemCount, Integer totalAmount, Timestamp createdAt, String exhibitionName, String ticketTypeName) {
		super();
		this.orderId = orderId;
		this.orderUlid = orderUlid;
		this.fullName = fullName;
		this.orderStatus = orderStatus;
		this.itemCount = itemCount;
		this.totalAmount = totalAmount;
		this.createdAt = createdAt;
		this.exhibitionName = exhibitionName;
		this.ticketTypeName = ticketTypeName;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getOrderUlid() {
		return orderUlid;
	}
	public void setOrderUlid(String orderUlid) {
		this.orderUlid = orderUlid;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Long getItemCount() {
		return itemCount;
	}
	public void setItemCount(Long itemCount) {
		this.itemCount = itemCount;
	}
	public String getExhibitionName() {
		return exhibitionName;
	}
	public void setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
	}
	public String getTicketTypeName() {
		return ticketTypeName;
	}
	public void setTicketTypeName(String ticketTypeName) {
		this.ticketTypeName = ticketTypeName;
	}
}
