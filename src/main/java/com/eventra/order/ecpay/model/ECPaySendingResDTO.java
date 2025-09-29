package com.eventra.order.ecpay.model;

import java.util.Map;

public class ECPaySendingResDTO {
	private String status;
	private String action;
	private String method;
	private Map<String, String> fields;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Map<String, String> getFields() {
		return fields;
	}
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
	
	public static class Builder {
		private String status;
		private String action;
		private String method;
		private Map<String, String> fields;
		
		public Builder status(String status) {
			this.status = status;
			return this;
		}
		public Builder action(String action) {
			this.action = action;
			return this;
		}
		public Builder method(String method) {
			this.method = method;
			return this;
		}
		public Builder fields(Map<String, String> fields) {
			this.fields = fields;
			return this;
		}
		public ECPaySendingResDTO build() {
			ECPaySendingResDTO vo = new ECPaySendingResDTO();
			vo.setStatus(this.status);
			vo.setAction(this.action);
			vo.setMethod(this.method);
			vo.setFields(this.fields);
			return vo;
		}
	}
}
