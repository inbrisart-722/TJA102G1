package com.eventra.customerservice.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.customerservice.model.ChatMessageResDTO;
import com.eventra.customerservice.model.CustomerServiceService;

@RestController
@RequestMapping("/api/front-end/chat")
public class CustomerServiceController {

	private final AtomicInteger onlineCount;
	private final CustomerServiceService CS_SVC;
	
	public CustomerServiceController(AtomicInteger onlineCount, CustomerServiceService customerServiceService) {
		this.onlineCount = onlineCount;
		this.CS_SVC = customerServiceService;
	}
	
	@GetMapping("/getMessages")
	public ResponseEntity<List<ChatMessageResDTO>> getMessages(@RequestParam("timestamp") Long timestamp){
		
		List<ChatMessageResDTO> list = CS_SVC.getMessages(timestamp);
		
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/initCount")
	public ResponseEntity<Integer> initCount(){
		return ResponseEntity.ok(onlineCount.get());
	}
}
