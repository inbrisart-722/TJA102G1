package com.eventra.order.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import com.eventra.member.model.MemberVO;
import com.eventra.order_item.model.OrderItemVO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "`order`")
public class OrderVO implements Serializable{
	
	@Id
	@Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;
	
	@JsonIgnore
	@OneToMany(mappedBy="order")
	private Set<OrderItemVO> orderItems;

	@Column(name = "order_status")
	private String orderStatus;
	
//	@Column(name = "member_id", insertable = false, updatable = false)
//	private Integer memberId;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
	private MemberVO member;

	@Column(name = "total_amount")
	private Integer totalAmount;
	
	@Column(name = "total_quantity")
	private Integer totalQuantity;
	
	@Column(name = "cancelled_at", insertable = true, updatable = true, nullable = true)
	private Timestamp cancelledAt;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
	
	public OrderVO() {}
	
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public MemberVO getMember() {
		return member;
	}
	public void setMember(MemberVO member) {
		this.member = member;
//		this.memberId = (member != null ? member.getMemberId() : null);
	}
//	public Integer getMemberId() {
//		return memberId;
//	}
//	public void setMemberId(Integer memberId) {
//		this.memberId = memberId;
//	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Integer getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public Timestamp getCancelledAt() {
		return cancelledAt;
	}
	public void setCancelledAt(Timestamp cancelledAt) {
		this.cancelledAt = cancelledAt;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Set<OrderItemVO> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(Set<OrderItemVO> orderItems) {
		this.orderItems = orderItems;
	}

//	@Override
//	public String toString() {
//		return "OrderVO [orderId=" + orderId + ", orderStatus=" + orderStatus + ", memberId=" + memberId
//				+ ", totalAmount=" + totalAmount + ", totalQuantity=" + totalQuantity + ", cancelledAt=" + cancelledAt
//				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
//	}
	
}
