package com.eventra.cart_item.model;

public class GetCartItemResDTO {
	private Long cartItemId;
	private String photoPortrait;
	private String exhibitionName;
	private String ticketTypeName;
	private Integer quantity;
	private Integer price;
	private String expirationTime;
	
	public String getPhotoPortrait() {
		return photoPortrait;
	}
	public void setPhotoPortrait(String photoPortrait) {
		this.photoPortrait = photoPortrait;
	}
	public Long getCartItemId() {
		return cartItemId;
	}
	public void setCartItemId(Long cartItemId) {
		this.cartItemId = cartItemId;
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
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public String getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	// --- Builder ---
    public static class Builder {
        private Long cartItemId;
        private String photoPortrait;
        private String exhibitionName;
        private String ticketTypeName;
        private Integer quantity;
        private Integer price;
        private String expirationTime;

        public Builder photoPortrait(String photoPortrait) {
            this.photoPortrait = photoPortrait;
            return this;
        }
        
        public Builder cartItemId(Long cartItemId) {
            this.cartItemId = cartItemId;
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

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }
        public Builder price(Integer price) {
        	this.price = price;
        	return this;
        }
        public Builder expirationTime(String expirationTime) {
        	this.expirationTime = expirationTime;
        	return this;
        }

        public GetCartItemResDTO build() {
            GetCartItemResDTO dto = new GetCartItemResDTO();
            dto.setCartItemId(this.cartItemId);
            dto.setPhotoPortrait(this.photoPortrait);
            dto.setExhibitionName(this.exhibitionName);
            dto.setTicketTypeName(this.ticketTypeName);
            dto.setQuantity(this.quantity);
            dto.setPrice(this.price);
            dto.setExpirationTime(this.expirationTime);
            return dto;
        }
    }
}
