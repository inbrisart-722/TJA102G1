package com.eventra.order.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.order.model.GetAllOrderResDTO;
import com.eventra.order.model.OrderProvider;
import com.eventra.order.model.OrderService;
import com.eventra.order.model.OrderStatus;

@RestController
@RequestMapping("/api/front-end")
public class OrderRestController {

//	private static final Integer TEST_MEMBER = 3;
	
	private final OrderService ORDER_SERVICE;
	
	public OrderRestController(OrderService orderService) {
		this.ORDER_SERVICE = orderService;
	}
	
	@GetMapping("/protected/order/getOrderProvider")
	public ResponseEntity<OrderProvider> getOrderProvider(@RequestParam("orderUlid") String orderUlid){
		return ResponseEntity.ok(ORDER_SERVICE.getOrderProvider(orderUlid));
	}
	
	@GetMapping("/protected/order/getAllOrder")
	public List<GetAllOrderResDTO> getAllOrder(Principal principal){
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		return ORDER_SERVICE.getAllOrderByMemberId(memberId);
	}
	
/* ************************* 以下皆與金流相關 ************************* */
	
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
