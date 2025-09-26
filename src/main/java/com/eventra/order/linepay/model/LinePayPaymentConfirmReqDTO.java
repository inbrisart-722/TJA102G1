package com.eventra.order.linepay.model;

public class LinePayPaymentConfirmReqDTO {
	private Integer amount;
	private String currency; // "TWD"
	
	public Integer getAmount() {
		return amount;
	}
	public LinePayPaymentConfirmReqDTO setAmount(Integer amount) {
		this.amount = amount;
		return this;
	}
	public String getCurrency() {
		return currency;
	}
	public LinePayPaymentConfirmReqDTO setCurrency(String currency) {
		this.currency = currency;
		return this;
	}
}
