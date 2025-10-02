package com.eventra.order.linepay.model;

public enum LinePaySendingStatus {
	SUCCESS("付款請求成功"), // 這條不會用到 message
	FAILURE_CART_ITEM("指定結帳的購物車明細已失效"),
	FAILURE_LINE_PAY("呼叫 LINE PAY 付款請求時發生錯誤，請稍後再試"),
	FAILURE_NOT_FOUND("找不到此筆訂單，或已經無法再次嘗試付款");
	
	private String message;
	
	LinePaySendingStatus(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
