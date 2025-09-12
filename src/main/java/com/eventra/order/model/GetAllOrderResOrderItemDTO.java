package com.eventra.order.model;

public class GetAllOrderResOrderItemDTO {
	private String orderItemUlid;
	private String ticketCode;
	private String ticketTypeName;
//	private Integer unitPrice; // admin 頁面看要不要補
	
	public String getOrderItemUlid() {
		return orderItemUlid;
	}
	public GetAllOrderResOrderItemDTO setOrderItemUlid(String orderItemUlid) {
		this.orderItemUlid = orderItemUlid;
		return this;
	}
	public String getTicketCode() {
		return ticketCode;
	}
	public GetAllOrderResOrderItemDTO setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
		return this;
	}
	public String getTicketTypeName() {
		return ticketTypeName;
	}
	public GetAllOrderResOrderItemDTO setTicketTypeName(String ticketTypeName) {
		this.ticketTypeName = ticketTypeName;
		return this;
	}
}
