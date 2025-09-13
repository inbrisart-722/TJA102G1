package com.eventra.order.model;

import java.util.List;

public class GetAllOrderResGroupedDTO {
	private GetAllOrderResExhibitionDTO exhibitionDTO;
	private List<GetAllOrderResOrderItemDTO> orderItemsDTO;
	
	public GetAllOrderResExhibitionDTO getExhibitionDTO() {
		return exhibitionDTO;
	}
	public GetAllOrderResGroupedDTO setExhibitionDTO(GetAllOrderResExhibitionDTO exhibitionDTO) {
		this.exhibitionDTO = exhibitionDTO;
		return this;
	}
	public List<GetAllOrderResOrderItemDTO> getOrderItemsDTO() {
		return orderItemsDTO;
	}
	public GetAllOrderResGroupedDTO setOrderItemsDTO(List<GetAllOrderResOrderItemDTO> orderItemsDTO) {
		this.orderItemsDTO = orderItemsDTO;
		return this;
	}
}
