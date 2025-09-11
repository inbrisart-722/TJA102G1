package com.eventra.order.model;

import java.util.List;

public class ECPaySendingReqDTO {
	private List<Integer> cartItemIds;

	public List<Integer> getCartItemIds() {
		return cartItemIds;
	}
	public void setCartItemIds(List<Integer> cartItemIds) {
		this.cartItemIds = cartItemIds;
	}
}
