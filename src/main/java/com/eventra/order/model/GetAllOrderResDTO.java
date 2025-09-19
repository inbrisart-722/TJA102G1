package com.eventra.order.model;

import java.util.List;

public class GetAllOrderResDTO {
	private String orderUlid;
	private OrderStatus orderStatus;
	private Integer totalAmount;
	private Integer totalQuantity;
	private List<GetAllOrderResGroupedDTO> groups;
	
	public String getOrderUlid() {
		return orderUlid;
	}
	public GetAllOrderResDTO setOrderUlid(String orderUlid) {
		this.orderUlid = orderUlid;
		return this;
	}
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	public GetAllOrderResDTO setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
		return this;
	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public GetAllOrderResDTO setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}
	public Integer getTotalQuantity() {
		return totalQuantity;
	}
	public GetAllOrderResDTO setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
		return this;
	}
	public List<GetAllOrderResGroupedDTO> getGroups() {
		return groups;
	}
	public GetAllOrderResDTO setGroups(List<GetAllOrderResGroupedDTO> groups) {
		this.groups = groups;
		return this;
	}
	
	// 前端以這樣的格式更好處理（序列化成 items 很方便）
	
	//	=> List<ExhibitionGroupDTO> 
	//	public class ExhibitionGroupDTO
	//	private ExhibitionDTO exhibition;
	//	private List<OrderItemDTO> items;
}
