package com.eventra.exhibitiontickettype.model;

public class ExhibitionTicketTypeDTO {
	
	private String ticketTypeName;
	private Integer price;
	
	public String getTicketTypeName() {
		return ticketTypeName;
	}
	public ExhibitionTicketTypeDTO setTicketTypeName(String ticketTypeName) {
		this.ticketTypeName = ticketTypeName;
		return this;
	}
	public Integer getPrice() {
		return price;
	}
	public ExhibitionTicketTypeDTO setPrice(Integer price) {
		this.price = price;
		return this;
	}
	
}
