package com.eventra.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.order.model.OrderService;
import com.eventra.order.model.OrderVO;

@Controller
@RequestMapping("/front-end")
public class OrderController {

	private static final Integer TEST_MEMBER = 3;
	
	private final OrderService ORDER_SERVICE;
	
	public OrderController(OrderService orderService) {
		this.ORDER_SERVICE = orderService;
	}
	
	/* ************************* 以下皆與綠界相關 ************************* */
	
	// 給綠界打的
	@PostMapping("/ECPay/OrderResultURL") // 對應 ECPay -> OrderResultURL client-to-server
	public String ECPayOrderResultURL(@RequestParam("merchantTradeNo") String merchantTradeNo, Model model) {
		System.out.println("OrderResultURL");
		// 不驗簽，直接轉中繼頁面，回打 api 查 ReturnURL 那條的狀態
//		OrderVO orderVO = ORDER_SERVICE.getOneOrderByTradeNo(merchantTradeNo);
//		model.addAttribute("orderVO", orderVO);
		return "redirect:/front-end/order_pending?merchantTradeNo=" + merchantTradeNo;
	}
	
	@GetMapping("/order_pending")
	public String toPending(@RequestParam("merchantTradeNo") String merchantTradeNo, Model model) {
		OrderVO orderVO = ORDER_SERVICE.getOneOrderByTradeNo(merchantTradeNo);
		model.addAttribute("orderVO", orderVO);
		return "front-end/order_pending";
	}
	
	// 給綠界打的
	@GetMapping("/ECPay/ClientBackURL") // 做為 OrderResultURL 的 rollback（基本上不會用到）
	public String ECPayClientBackURL(@RequestParam("merchantTradeNo") String merchantTradeNo, Model model) {
		System.out.println("ClientBackURL");
		// 導回時不會帶付款結果到此網址，只是將頁面導回而已。
		// 設定此參數，發生簡訊OTP驗證失敗時，頁面上會顯示[返回商店]的按鈕。
		// 處理訂單 -> 付款失敗?? 還是一樣走直接導並且打 api 等 ResultURL ?
//		OrderVO orderVO = ORDER_SERVICE.getOneOrderByTradeNo(merchantTradeNo);
//		model.addAttribute("orderVO", orderVO);
		return "redirect:/front-end/order_pending?merchantTradeNo=" + merchantTradeNo;
	}
	
	// 要檢查真的成功嗎！才show 怕有人亂輸入我就亂給
	@GetMapping("/order_success")
	public String toOrderSuccess(@RequestParam("merchantTradeNo") String merchantTradeNo, Model model){
		OrderVO orderVO = ORDER_SERVICE.getOneOrderByTradeNo(merchantTradeNo);
		model.addAttribute("orderVO", orderVO);
		return "front-end/order_success"; // Spring 會自動在它前面加上 src/main/resources/templates/
	}
	
	// 要檢查真的失敗嗎！才show 怕有人亂輸入我就亂給
	@GetMapping("/order_failure")
	public String toOrderFailure(@RequestParam("merchantTradeNo") String merchantTradeNo, Model model){
		OrderVO orderVO = ORDER_SERVICE.getOneOrderByTradeNo(merchantTradeNo);
		model.addAttribute("orderVO", orderVO);
		return "front-end/order_failure";
	}
}
