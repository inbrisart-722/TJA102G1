package com.eventra.order.ecpay.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.order.ecpay.model.ECPayCallbackReqDTO;
import com.eventra.order.ecpay.model.ECPaySendingReqDTO;
import com.eventra.order.ecpay.model.ECPaySendingResDTO;
import com.eventra.order.ecpay.model.ECPayService;

@RestController
@RequestMapping("/api/front-end")
public class ECPayRestController {

	private final ECPayService ECPAY_SERVICE;
	
	public ECPayRestController(ECPayService ecpayService) {
		this.ECPAY_SERVICE = ecpayService;
	}
	
	@PostMapping("/protected/order/ECPay/resending")
	public ECPaySendingResDTO ECPayResending(@RequestBody String orderUlid, Principal principal) {
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		System.out.println("ECPay Resending: " + memberId + "!!!!!!!!!!");
		// 有需要判斷 memberId 不吻合不給送？
		ECPaySendingResDTO res = ECPAY_SERVICE.ECPayResending(orderUlid);
		return res;
	}
	
	@PostMapping("/protected/order/ECPay/sending")
	public ECPaySendingResDTO ECPaySending(@RequestBody ECPaySendingReqDTO req, Principal principal) {
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		System.out.println("ECPay sending: " + memberId + "!!!!!!!!!!");
		ECPaySendingResDTO res = ECPAY_SERVICE.ECPaySending(req, memberId);
		return res;
	}
	// 前端 送 CartItemIds -> List<Integer> 
	// 後端 回 status + action + method + fields 給前端拼 form -> form.submit() -> 進綠界
	
	@PostMapping("/order/ECPay/ReturnURL") // 對應 ECPay -> ReturnURL server-to-server
	public String ECPayReturnURL(@ModelAttribute ECPayCallbackReqDTO req) {
		System.out.println("ReturnURL, processing...");
		
		// 測試 OrderResultURL Long Polling 用
//		try{Thread.sleep(5000);} 
		// 不知道為啥也會卡住 OrderResultURL...
		// https://developers.ecpay.com.tw/?p=2878
		// ReturnURL和 OrderResultURL沒有固定的先後順序，會依當下連線速度與系統執行速度而定
//		catch(InterruptedException e) {System.out.println(e.toString());}
		
		String returnCode = ECPAY_SERVICE.ECPayReturnURL(req);
		System.out.println("returnCode: " + returnCode);
		return "1|OK";
	}
}
