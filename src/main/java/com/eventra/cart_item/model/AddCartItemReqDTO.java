package com.eventra.cart_item.model;

import java.util.Map;

public class AddCartItemReqDTO {
	private Integer exhibitionId;
	private Map<String, Integer> ticketDatas;
	
	public Integer getExhibitionId() {
		return exhibitionId;
	}
	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}
	public Map<String, Integer> getTicketDatas() {
		return ticketDatas;
	}
	public void setTicketDatas(Map<String, Integer> ticketDatas) {
		this.ticketDatas = ticketDatas;
	}
}
