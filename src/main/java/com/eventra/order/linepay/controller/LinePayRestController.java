package com.eventra.order.linepay.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.order.linepay.model.LinePaySendingReqDTO;
import com.eventra.order.linepay.model.LinePaySendingResDTO;
import com.eventra.order.linepay.model.LinePayService;

@RestController
@RequestMapping("/api/front-end")
public class LinePayRestController {

	private final LinePayService LINE_PAY_SVC;
	
	public LinePayRestController(LinePayService linePayService) {
		this.LINE_PAY_SVC = linePayService;
	}
	
	@PostMapping("/protected/linepay/payment-request")
	public ResponseEntity<LinePaySendingResDTO> paymentRequest(@RequestBody LinePaySendingReqDTO req, Principal principal){
		
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		
		// 沒有有效的購物車 id -> null
		// 失敗: 0000以外的狀態碼 -> res.getReturnMessage()
		// 成功: 0000 -> paymentUrl.web
		
		return ResponseEntity.ok(LINE_PAY_SVC.paymentRequest(req, memberId));
	}
}
