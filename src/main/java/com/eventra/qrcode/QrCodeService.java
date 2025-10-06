package com.eventra.qrcode;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.order_item.model.OrderItemRepository;
import com.eventra.order_item.model.OrderItemVO;

@Service
@Transactional
public class QrCodeService {
	
	private final OrderItemRepository ORDER_ITEM_REPO;
	
	public QrCodeService(OrderItemRepository orderItemRepo) {
		this.ORDER_ITEM_REPO = orderItemRepo;
	}
	
	public QrCodeValidationResponse validateByTicketCode(String ticketCode, String curBusinessIdNumber){
		Optional<OrderItemVO> orderItemOp = ORDER_ITEM_REPO.findByTicketCode(ticketCode);
		
		if(!orderItemOp.isPresent()) return QrCodeValidationResponse.of(QrCodeValidationResult.NOT_EXIST);
		
		OrderItemVO orderItem = orderItemOp.get();
		LocalDateTime qrCodeUsedAt = orderItem.getQrCodeUsedAt(); 
		if(qrCodeUsedAt != null) return QrCodeValidationResponse.of(QrCodeValidationResult.USED);
		
		ExhibitionVO exhibition = orderItem.getExhibitionTicketType().getExhibition();
		String businessIdNumber = exhibition.getExhibitorVO().getBusinessIdNumber();
		
		if(!curBusinessIdNumber.equals(businessIdNumber)) return QrCodeValidationResponse.of(QrCodeValidationResult.FORBIDDEN);
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startTime = exhibition.getStartTime();
		if(now.isBefore(startTime)) return QrCodeValidationResponse.of(QrCodeValidationResult.VALID_BUT_NOT_START);
		
		LocalDateTime endTime = exhibition.getEndTime();
		if(now.isAfter(endTime)) return QrCodeValidationResponse.of(QrCodeValidationResult.VALID_BUT_END);
		
		// 以下為成功狀況
		
		// 1. 更新 db
		orderItem.setQrCodeUsedAt(now);
		
		// 2. 裝 qrcode_success 頁面所需之 dto
		String exhibitorName = exhibition.getExhibitorVO().getExhibitorRegistrationName();
		if(exhibitorName == null) exhibitorName = exhibition.getExhibitorVO().getCompanyName();
		
		QrCodeDTO dto = new QrCodeDTO();
		dto.setExhibitionName(exhibition.getExhibitionName());
		dto.setExhibitorName(exhibitorName);
		dto.setMemberNickname(orderItem.getOrder().getMember().getNickname());
		dto.setTicketCode(ticketCode);
		dto.setQrCodeUsedAt(now);
		
		// 3. 回傳
		return QrCodeValidationResponse.success(dto);
	}
}
