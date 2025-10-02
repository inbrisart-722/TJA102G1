package com.eventra.order_item.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eventra.member.model.MemberVO;
import com.eventra.order.model.OrderStatus;

public interface OrderItemRepository extends JpaRepository<OrderItemVO, Integer> {

//	boolean existsByOrder_Member_MemberIdAndExhibition_ExhibitionIdAndOrder_OrderStatusIn
//	(Integer memberId, Integer exhibitionId, Collection<String> orderStatuses);
	// 建立 Enum of orderStatus =>
	boolean existsByOrder_Member_MemberIdAndExhibitionTicketType_ExhibitionIdAndOrder_OrderStatus(Integer memberId,
			Integer exhibitionId, OrderStatus orderStatus);

	Slice<OrderItemVO> findByOrder_MemberAndOrder_OrderUlidAndOrder_OrderStatus(MemberVO member, String orderUlid, OrderStatus status,
			Pageable pageable);
}
