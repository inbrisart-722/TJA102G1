package com.eventra.order.linepay.model;

import java.util.List;

// 目前欄位與 ECPaySendingReqDTO 相同
public class LinePaySendingReqDTO {
	private List<Integer> cartItemIds;

	public List<Integer> getCartItemIds() {
		return cartItemIds;
	}
	public void setCartItemIds(List<Integer> cartItemIds) {
		this.cartItemIds = cartItemIds;
	}
}
