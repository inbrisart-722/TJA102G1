package com.eventra.cart_item.model;

import java.sql.Timestamp;

public class CartItemRedisVO {
	private Long cartItemId;
	private Integer memberId;
	private Integer exhibitionTicketTypeId;
	
	// 多存，好取
	private Integer exhibitionId;
	// 多存，避免清單查詢太久
	private String exhibitionName; 
	private	String ticketTypeName;
	
	private Integer price; 
	private Integer quantity;
	private Long createdAt;
	
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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
	public Long getCartItemId() {
		return cartItemId;
	}
	public void setCartItemId(Long cartItemId) {
		this.cartItemId = cartItemId;
	}
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public Integer getExhibitionTicketTypeId() {
		return exhibitionTicketTypeId;
	}
	public void setExhibitionTicketTypeId(Integer exhibitionTicketTypeId) {
		this.exhibitionTicketTypeId = exhibitionTicketTypeId;
	}
	public Integer getExhibitionId() {
		return exhibitionId;
	}
	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}
	public Long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	
	 // --- Builder ---
    public static class Builder {
        private Long cartItemId;
        private Integer memberId;
        private Integer exhibitionId;
        private Integer exhibitionTicketTypeId;
        private String exhibitionName;
        private String ticketTypeName;
        private Integer price;
        private Integer quantity;
        private Long createdAt;

        public Builder cartItemId(Long cartItemId) {
            this.cartItemId = cartItemId;
            return this;
        }
        public Builder memberId(Integer memberId) {
            this.memberId = memberId;
            return this;
        }
        public Builder exhibitionId(Integer exhibitionId) {
            this.exhibitionId = exhibitionId;
            return this;
        }
        public Builder exhibitionTicketTypeId(Integer exhibitionTicketTypeId) {
        	this.exhibitionTicketTypeId = exhibitionTicketTypeId;
        	return this;
        }
        public Builder exhibitionName(String exhibitionName) {
            this.exhibitionName = exhibitionName;
            return this;
        }
        public Builder ticketTypeName(String ticketTypeName) {
            this.ticketTypeName = ticketTypeName;
            return this;
        }
        public Builder price(Integer price) {
            this.price = price;
            return this;
        }
        public Builder quantity(Integer quantity) {
        	this.quantity = quantity;
        	return this;
        }
        public Builder createdAt(Long createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CartItemRedisVO build() {
            CartItemRedisVO obj = new CartItemRedisVO();
            obj.setCartItemId(this.cartItemId);
            obj.setMemberId(this.memberId);
            obj.setExhibitionTicketTypeId(this.exhibitionTicketTypeId);
            obj.setExhibitionId(this.exhibitionId);
            obj.setExhibitionName(this.exhibitionName);
            obj.setTicketTypeName(this.ticketTypeName);
            obj.setPrice(this.price);
            obj.setQuantity(quantity);
            obj.setCreatedAt(this.createdAt);
            return obj;
        }
    }
}
