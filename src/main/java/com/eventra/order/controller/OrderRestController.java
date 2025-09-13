package com.eventra.order.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.cart_item.model.CartItemService;
import com.eventra.order.model.ECPayCallbackReqDTO;
import com.eventra.order.model.ECPaySendingReqDTO;
import com.eventra.order.model.ECPaySendingResDTO;
import com.eventra.order.model.GetAllOrderResDTO;
import com.eventra.order.model.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderRestController {

	private static final String MERCHANT_ID = "2000132";
	private static final Integer TEST_MEMBER = 3;
	
	private final OrderService ORDER_SERVICE;
	private final CartItemService CART_ITEM_SERVICE;
	
	public OrderRestController(OrderService orderService, CartItemService cartItemService) {
		this.ORDER_SERVICE = orderService;
		this.CART_ITEM_SERVICE = cartItemService; 
	}
	
	@GetMapping("getAllOrder")
	public List<GetAllOrderResDTO> getAllOrder(){
		return ORDER_SERVICE.getAllOrderByMemberId(TEST_MEMBER);
	}
	
/* ************************* 以下皆與綠界相關 ************************* */
	@PostMapping("ECPay/resending")
	public ECPaySendingResDTO ECPayResending(@RequestBody String orderUlid) {
		System.out.println("re-sending");
		ECPaySendingResDTO res = ORDER_SERVICE.ECPayResending(orderUlid);
		return res;
	}
	
	@PostMapping("ECPay/sending")
	public ECPaySendingResDTO ECPaySending(@RequestBody ECPaySendingReqDTO req) {
		System.out.println("sending");
		ECPaySendingResDTO res = ORDER_SERVICE.ECPaySending(req, TEST_MEMBER);
		return res;
	}
	// 前端 送 CartItemIds -> List<Integer> 
	// 後端 回 status + action + method + fields 給前端拼 form -> form.submit() -> 進綠界
	
	@PostMapping("ECPay/ReturnURL") // 對應 ECPay -> ReturnURL server-to-server
	public String ECPayReturnURL(@ModelAttribute ECPayCallbackReqDTO req) {
		System.out.println("ReturnURL, processing...");
		
		// 測試 OrderResultURL Long Polling 用
//		try{Thread.sleep(5000);} 
		// 不知道為啥也會卡住 OrderResultURL...
		// https://developers.ecpay.com.tw/?p=2878
		// ReturnURL和 OrderResultURL沒有固定的先後順序，會依當下連線速度與系統執行速度而定
//		catch(InterruptedException e) {System.out.println(e.toString());}
		
		String returnCode = ORDER_SERVICE.ECPayReturnURL(req);
		System.out.println("returnCode: " + returnCode);
		return "1|OK";
	}
	
	@GetMapping("checkOrderStatus")
	public ResponseEntity<Map<String, String>> checkOrderStatus(@RequestParam("merchantTradeNo") String merchantTradeNo) {
		System.out.println("checking order status...");
		
		Map<String, String> res = new HashMap<>();
		String orderStatus = null;
		
		// 阻塞式 Long Polling（非 WebFlux）-> 高併發 效率不佳 -> SSE / WebSocket
		for(int i = 0; i < 30; i++) {
			orderStatus = ORDER_SERVICE.checkOrderStatus(merchantTradeNo);
			if(!"待付款".equals(orderStatus)) {
				res.put("orderStatus", orderStatus);
				return ResponseEntity.ok(res); 
			}
			try {Thread.sleep(1000);}
			catch(InterruptedException e) {System.out.println(e.toString());}
		}
		
		// 如果 OrderResultURL 導前端 打回來 過了 30 秒 ReturnURL 都還沒更新狀態
		res.put("orderStatus", orderStatus);
		return ResponseEntity.ok(res);
	}
}
