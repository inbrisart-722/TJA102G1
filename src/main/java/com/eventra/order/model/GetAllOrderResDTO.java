package com.eventra.order.model;

import java.util.List;

import com.eventra.order_item.model.OrderItemVO;

public class GetAllOrderResDTO {
	private String orderUlid;
	private String orderStatus;
	private Integer totalAmount;
	private Integer totalQuantity;
//	private Map<ExhibitionDTO, List<OrderItemDTO>> orderItemsGrouped;
	
	// 前端以這樣的格式更好處理（序列化成 items 很方便）
	
//	Map<ExhibitionDTO, List<OrderItemDTO>>
//	=> List<ExhibitionGroupDTO> 
//	public class ExhibitionGroupDTO
//		private ExhibitionDTO exhibition;
//		private List<OrderItemDTO> items;
}
