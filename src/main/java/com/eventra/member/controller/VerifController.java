package com.eventra.member.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.member.model.SendVerifCodeReqDTO;
import com.eventra.member.model.VerifService;
import com.eventra.member.model.VerificationResult;

@Controller
@RequestMapping("/front-end")
public class VerifController {
	
	private final VerifService VERIF_SERVICE;
	
	public VerifController(VerifService verifService) {
		this.VERIF_SERVICE = verifService;
	}
	
	// Mail (1)
	@GetMapping("/verif-registration")
	public String registration(@RequestParam("token") String token, Model model) {
		
		VerificationResult res = VERIF_SERVICE.verifRegistration(token);
		
		System.out.println(res.getMessage());
		
		// 1. 成功（需要 token 不需要 message）
		if(VerificationResult.SUCCESS == res) {
			return "redirect:/front-end/register2?token=" + token;
		}
		
		// 2. 失敗（需要 message 不需要 email)
		model.addAttribute("message", res.getMessage());
		// 2-1. 失敗 - token 用途錯誤
		// 2-2. 失敗 - token 過期或失效等其他錯誤
		return "front-end/verif_failure";
	}
	
	// Mail (2)
	@GetMapping("/verif-forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody SendVerifCodeReqDTO req) {
		return null;
	}
	
	// Mail (3)
	@GetMapping("/verif-change-mail")
	public ResponseEntity<String> changeMail(@RequestBody SendVerifCodeReqDTO req) {
		return null;
	}
}
