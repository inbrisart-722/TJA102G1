package com.eventra.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.order.model.OrderService;
import com.eventra.order.model.OrderVO;

@Controller
@RequestMapping("/front-end")
public class OrderController {

//	private static final Integer TEST_MEMBER = 3;
	
	private final OrderService ORDER_SERVICE;
	
	public OrderController(OrderService orderService) {
		this.ORDER_SERVICE = orderService;
	}
	
	/* ************************* 以下皆與金流相關 ************************* */
	
	@GetMapping("/order_pending")
	public String toPending(@RequestParam("providerOrderId") String providerOrderId, Model model) {
		OrderVO orderVO = ORDER_SERVICE.getOneOrderByProviderOrderId(providerOrderId);
		model.addAttribute("orderVO", orderVO);
		return "front-end/order_pending";
	}
	
	// 要檢查真的成功嗎！才show 怕有人亂輸入我就亂給
	@GetMapping("/order_success")
	public String toOrderSuccess(@RequestParam("providerOrderId") String providerOrderId, Model model){
		OrderVO orderVO = ORDER_SERVICE.getOneOrderByProviderOrderId(providerOrderId);
		model.addAttribute("orderVO", orderVO);
		return "front-end/order_success";
	}
	
	// 要檢查真的失敗嗎！才show 怕有人亂輸入我就亂給
	@GetMapping("/order_failure")
	public String toOrderFailure(@RequestParam("providerOrderId") String providerOrderId, Model model){
		OrderVO orderVO = ORDER_SERVICE.getOneOrderByProviderOrderId(providerOrderId);
		model.addAttribute("orderVO", orderVO);
		return "front-end/order_failure";
	}
}
