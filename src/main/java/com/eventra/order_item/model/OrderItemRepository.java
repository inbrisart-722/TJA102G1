package com.eventra.order_item.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventra.order.model.OrderStatus;

public interface OrderItemRepository extends JpaRepository<OrderItemVO, Integer>{
	
//	boolean existsByOrder_Member_MemberIdAndExhibition_ExhibitionIdAndOrder_OrderStatusIn
//	(Integer memberId, Integer exhibitionId, Collection<String> orderStatuses);
	// 建立 Enum of orderStatus => 
	boolean existsByOrder_Member_MemberIdAndExhibitionTicketType_ExhibitionIdAndOrder_OrderStatus
	(Integer memberId, Integer exhibitionId, OrderStatus orderStatus);
}
