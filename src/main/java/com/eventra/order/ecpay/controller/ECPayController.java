package com.eventra.order.ecpay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/front-end")
public class ECPayController {

	// 給綠界打的
	@PostMapping("/ECPay/OrderResultURL") // 對應 ECPay -> OrderResultURL client-to-server
	public String ECPayOrderResultURL(@RequestParam("providerOrderId") String providerOrderId, Model model) {
		System.out.println("OrderResultURL");
		return "redirect:/front-end/order_pending?providerOrderId=" + providerOrderId;
	}

	// 給綠界打的
	@GetMapping("/ECPay/ClientBackURL") // 做為 OrderResultURL 的 rollback（基本上不會用到）
	public String ECPayClientBackURL(@RequestParam("providerOrderId") String providerOrderId, Model model) {
		System.out.println("ClientBackURL");
		// 導回時不會帶付款結果到此網址，只是將頁面導回而已。
		// 設定此參數，發生簡訊OTP驗證失敗時，頁面上會顯示[返回商店]的按鈕。
		// 處理訂單 -> 付款失敗?? 還是一樣走直接導並且打 api 等 ResultURL ?
		return "redirect:/front-end/order_pending?providerOrderId=" + providerOrderId;
	}
}
