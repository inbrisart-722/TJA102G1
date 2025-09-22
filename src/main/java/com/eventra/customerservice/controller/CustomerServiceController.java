package com.eventra.customerservice.controller;

import java.util.List;

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

	private final CustomerServiceService CS_SVC;
	
	public CustomerServiceController(CustomerServiceService customerServiceService) {
		this.CS_SVC = customerServiceService;
	}
	
	@GetMapping("/getMessages")
	public ResponseEntity<List<ChatMessageResDTO>> getMessages(@RequestParam("timestamp") Long timestamp){
		
		List<ChatMessageResDTO> list = CS_SVC.getMessages(timestamp);
		
		return ResponseEntity.ok(list);
	}
}
