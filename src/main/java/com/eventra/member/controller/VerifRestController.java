package com.eventra.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.member.model.CheckIfSendableResDTO;
import com.eventra.member.model.SendVerifCodeReqDTO;
import com.eventra.member.model.VerifService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/front-end")
public class VerifRestController {
	
	private final VerifService VERIF_SERVICE;
	
	public VerifRestController(VerifService verifService) {
		this.VERIF_SERVICE = verifService;
	}
	
	@GetMapping("/verif/check-if-sendable")
	public ResponseEntity<CheckIfSendableResDTO> checkIfSendable(@RequestParam("email") String email){
		
		CheckIfSendableResDTO res = VERIF_SERVICE.checkIfSendable(email);
		System.out.println("CHECK-IF-SENDABLE: " + res.isAllowed());
		// isAllowed, remaining 兩種參數回傳
		return ResponseEntity.status(200).body(res);
	}
	
	// Mail (1)
	@PostMapping("/verif/send-verif-code/registration")
	public ResponseEntity<String> registration(@RequestBody SendVerifCodeReqDTO req) throws MessagingException{
		
		String res = VERIF_SERVICE.sendVerifRegistration(req);
		System.out.println("VERIF-REGISTRATION SENDING: " + res);
		// 回傳給前端 "SUCCESS" or "FAILURE" -> 前端依此判斷 驗證信是否發送成功
		return ResponseEntity.status(200).body(res);
	}
	
	// Mail (2)
	@PostMapping("/verif/send-verif-code/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody SendVerifCodeReqDTO req) {
		return null;
	}
	
	// Mail (3)
	@PostMapping("/protected/verif/send-verif-code/change-mail")
	public ResponseEntity<String> changeMail(@RequestBody SendVerifCodeReqDTO req) {
		return null;
	}
}
