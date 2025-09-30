package com.eventra.order.linepay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.order.linepay.model.LinePayService;

@Controller
@RequestMapping("/api/front-end")
public class LinePayController {
	
	private final LinePayService LINE_PAY_SVC;
	
	public LinePayController(LinePayService linePayService) {
		this.LINE_PAY_SVC = linePayService;
	}
	
	// payment request -> 用戶轉導 linepay 頁面，授權"成功" -> confirm-url -> 還要再打 confirm api 同時 capture -> 先去 pending
	// 送去 OrderController 與 ECPay 統一處理
	@GetMapping("/linepay/confirm-url")
	public String confirmUrl(@RequestParam("providerOrderId") String providerOrderId) {
		System.out.println("linepay: confirm-url 成功訊息回來啦！");
		LINE_PAY_SVC.paymentConfirm(providerOrderId);
		// 打 confirm api 去同時 capture
		return "redirect:/front-end/order_pending?providerOrderId=" + providerOrderId;
	}
	// payment request -> 用戶轉導 linepay 頁面，授權"失敗" -> cancel-url -> 例如用戶直接取消付款等錯誤 -> 可以直接 order_failure
	// 送去 OrderController 與 ECPay 統一處理
	@GetMapping("/linepay/cancel-url")
	public String cancelUrl(@RequestParam("providerOrderId") String providerOrderId) {
		System.out.println("linepay: cancel-url 失敗訊息回來啦！");
		LINE_PAY_SVC.paymentCancel(providerOrderId);
		// 付款失敗（一大堆原因）例如 
//		1104	您的商店尚未在合作商店中心註冊成為合作商店。請確認輸入的credentials是否正確。
//		1105	該合作商店目前無法使用LINE Pay。
//		1106	請求標頭訊息有錯誤。
//		1110	該信用卡無法正常使用。
//		1124	金額訊息有誤。
		return "redirect:/front-end/order_failure?providerOrderId=" + providerOrderId;
	}
}
