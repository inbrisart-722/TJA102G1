package com.eventra.order.linepay.model;

// https://developers-pay.line.me/zh/online-api-v3/check-payment-request-status
public class LinePayPaymentRequestCheckResDTO {
	private String returnCode;
	private String returnMessage;
	
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getReturnMessage() {
		return returnMessage;
	}
	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}
}
