package com.eventra.member.verif.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.member.verif.model.AuthType;
import com.eventra.member.verif.model.SendVerifCodeReqDTO;
import com.eventra.member.verif.model.VerifService;
import com.eventra.member.verif.model.VerificationResult;

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

		VerificationResult res = VERIF_SERVICE.verifyToken(token, AuthType.REGISTRATION);

		System.out.println(res.getMessage());

		// 1. 成功（需要 token 不需要 message）
		if (VerificationResult.SUCCESS == res) {
			String email = VERIF_SERVICE.findEmailByToken(token);
			model.addAttribute("email", email);
			return "/front-end/register2";
//			return "redirect:/front-end/register2?token=" + token;
		}

		// 2. 失敗（需要 message 不需要 email)
		model.addAttribute("message", res.getMessage());
		// 2-1. 失敗 - token 用途錯誤
		// 2-2. 失敗 - token 過期或失效等其他錯誤
		return "front-end/verif_failure";
	}
	
	// Mail (2)
	@GetMapping("/verif-forgot-password")
	public String forgotPassword(@RequestParam("token") String token, Model model) {
		System.out.println(token);
		VerificationResult res = VERIF_SERVICE.verifyToken(token, AuthType.FORGOT_PASSWORD);

		System.out.println(res.getMessage());

		// 1. 成功（需要 token 不需要 message）
		if (VerificationResult.SUCCESS == res) {
			String email = VERIF_SERVICE.findEmailByToken(token);
			model.addAttribute("email", email);
			return "/front-end/forgot_password2";
//			return "redirect:/front-end/forgot-password2?token=" + token;
		}

		// 2. 失敗（需要 message 不需要 email)
		model.addAttribute("message", res.getMessage());
		// 2-1. 失敗 - token 用途錯誤
		// 2-2. 失敗 - token 過期或失效等其他錯誤
		return "front-end/verif_failure";
	}

	// Mail (3)
	@GetMapping("/verif-change-mail")
	public String changeMail(@RequestParam("token") String token, Model model) {
		VerificationResult res = VERIF_SERVICE.verifyToken(token, AuthType.CHANGE_MAIL);

		System.out.println(res.getMessage());

		// 1. 成功（需要 token 不需要 message）
		if (VerificationResult.SUCCESS == res) {
			return "redirect:/front-end/register2?token=" + token;
		}

		// 2. 失敗（需要 message 不需要 email)
		model.addAttribute("message", res.getMessage());
		// 2-1. 失敗 - token 用途錯誤
		// 2-2. 失敗 - token 過期或失效等其他錯誤
		return "front-end/verif_failure";
	}
}
