package com.eventra.order_item.model;

import java.io.Serializable;

import com.eventra.exhibition_ticket_type.model.ExhibitionTicketTypeVO;
import com.eventra.order.model.OrderVO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "`order_item`")
public class OrderItemVO implements Serializable{
	
	@Id
	@Column(name = "order_item_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderItemId;
	
//	@Column(name = "order_id", insertable = false, updatable = false)
//	private Integer orderId;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
	private OrderVO order;
	
//	@Column(name = "exhibition_ticket_type_id", insertable = false, updatable = false)
//	private Integer exhibitionTicketTypeId;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exhibition_ticket_type_id", referencedColumnName = "exhibition_ticket_type_id", nullable = false)
	private ExhibitionTicketTypeVO exhibitionTicketType;
	
	@Column(name = "unit_price", nullable = false)
	private Integer unitPrice;
	
	@Column(name = "ticket_code", nullable = false)
	private String ticketCode;

	public Integer getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}
	public void setOrder(OrderVO order) {
		this.order = order;
	}
	
	public OrderVO getOrder() {
		return order;
	}
	public void setOrderVO(OrderVO order) {
		this.order = order;
//		this.orderId = (orderVO != null ? orderVO.getOrderId() : null);
	}
//	public Integer getOrderId() {
//		return orderId;
//	}
//	public void setOrderId(Integer orderId) {
//		this.orderId = orderId;
//	}
//	public Integer getExhibitionTicketTypeId() {
//		return exhibitionTicketTypeId;
//	}
//	public void setExhibitionTicketTypeId(Integer exhibitionTicketTypeId) {
//		this.exhibitionTicketTypeId = exhibitionTicketTypeId;
//	}
	public ExhibitionTicketTypeVO getExhibitionTicketType() {
		return exhibitionTicketType;
	}
	public void setExhibitionTicketType(ExhibitionTicketTypeVO exhibitionTicketType) {
		this.exhibitionTicketType = exhibitionTicketType;
//		this.exhibitionTicketTypeId = (exhibitionTicketTypeVO != null ? exhibitionTicketTypeVO.getExhibitionTicketTypeId() : null);
	}
	public Integer getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Integer unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getTicketCode() {
		return ticketCode;
	}
	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}
	
}