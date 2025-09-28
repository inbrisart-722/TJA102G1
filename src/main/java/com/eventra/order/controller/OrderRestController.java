package com.eventra.order.controller;

import java.security.Principal;
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
import com.eventra.order.model.OrderStatus;

@RestController
@RequestMapping("/api/front-end")
public class OrderRestController {

//	private static final Integer TEST_MEMBER = 3;
	
	private final OrderService ORDER_SERVICE;
	private final CartItemService CART_ITEM_SERVICE;
	
	public OrderRestController(OrderService orderService, CartItemService cartItemService) {
		this.ORDER_SERVICE = orderService;
		this.CART_ITEM_SERVICE = cartItemService; 
	}
	
	@GetMapping("/protected/order/getAllOrder")
	public List<GetAllOrderResDTO> getAllOrder(Principal principal){
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		return ORDER_SERVICE.getAllOrderByMemberId(memberId);
	}
	
/* ************************* 以下皆與綠界相關 ************************* */
	@PostMapping("/protected/order/ECPay/resending")
	public ECPaySendingResDTO ECPayResending(@RequestBody String orderUlid, Principal principal) {
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		System.out.println("ECPay Resending: " + memberId + "!!!!!!!!!!");
		// 有需要判斷 memberId 不吻合不給送？
		ECPaySendingResDTO res = ORDER_SERVICE.ECPayResending(orderUlid);
		return res;
	}
	
	@PostMapping("/protected/order/ECPay/sending")
	public ECPaySendingResDTO ECPaySending(@RequestBody ECPaySendingReqDTO req, Principal principal) {
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		System.out.println("ECPay sending: " + memberId + "!!!!!!!!!!");
		ECPaySendingResDTO res = ORDER_SERVICE.ECPaySending(req, memberId);
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
		
		String returnCode = ORDER_SERVICE.ECPayReturnURL(req);
		System.out.println("returnCode: " + returnCode);
		return "1|OK";
	}
	
	@GetMapping("/protected/order/checkOrderStatus")
	public ResponseEntity<Map<String, Object>> checkOrderStatus(@RequestParam("providerOrderId") String providerOrderId) {
		System.out.println("checking order status...");
		
		Map<String, Object> res = new HashMap<>();
		OrderStatus orderStatus = null;
		
		// 阻塞式 Long Polling（非 WebFlux）-> 高併發 效率不佳 -> SSE / WebSocket
		for(int i = 0; i < 30; i++) {
			orderStatus = ORDER_SERVICE.checkOrderStatus(providerOrderId);
			if(orderStatus != OrderStatus.付款中) {
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
