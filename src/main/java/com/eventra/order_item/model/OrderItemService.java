package com.eventra.order_item.model;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.member.model.MemberRepository;
import com.eventra.member.model.MemberVO;
import com.eventra.order.model.OrderStatus;

@Service
@Transactional
public class OrderItemService {

	private final OrderItemRepository ORDER_ITEM_REPO;
	private final MemberRepository MEMBER_REPO;
	
	public OrderItemService(OrderItemRepository orderItemRepository, MemberRepository memberRepository) {
		this.ORDER_ITEM_REPO = orderItemRepository;
		this.MEMBER_REPO = memberRepository;
	}
	
	public Slice<OrderItemLineBotCarouselDTO> findOrderItemsByLineUserId(String lineUserId, String orderUlid, OrderStatus orderStatus, int page, int size){
		MemberVO member = MEMBER_REPO.findByLineUserId(lineUserId).orElseThrow();
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "order.createdAt"));
		
		Slice<OrderItemVO> orderItemsSlice = ORDER_ITEM_REPO.findByOrder_MemberAndOrder_OrderUlidAndOrder_OrderStatus(member, orderUlid, orderStatus, pageable);
		
		List<OrderItemLineBotCarouselDTO> dtos = orderItemsSlice.getContent().stream()
				.map(vo -> {
					ExhibitionVO exhibition = vo.getExhibitionTicketType().getExhibition();
							
					return new OrderItemLineBotCarouselDTO()
					.setPageUrl("http://localhost:8088/front-end/exhibitions?exhibitionId=" + exhibition.getExhibitionId())
					.setPhotoPortrait("https://scdn.line-apps.com/n/channel_devcenter/img/fx/01_3_movie.png") // 測試
//					.setPhotoPortrait(exhibition.getPhotoPortrait())
					.setExhibitionName(exhibition.getExhibitionName())
					.setOrderItemUlid(vo.getOrderItemUlid())
					.setTicketCode(vo.getTicketCode())
					.setStartTime(exhibition.getStartTime())
					.setEndTime(exhibition.getEndTime())
					.setLocation(exhibition.getLocation());
				}
				).collect(Collectors.toList());
		
		return new SliceImpl<>(dtos, pageable, orderItemsSlice.hasNext());
	}
}
