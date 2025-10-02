//package com.eventra.member.google;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequestMapping("/front-end/google")
//public class GoogleRestController {
//
//	private final GoogleService GOOGLE_SERVICE;
//	
//	public GoogleRestController(GoogleService googleService) {
//		this.GOOGLE_SERVICE = googleService;
//	}
//	
//	@GetMapping("/callback")
//	public String callback(@RequestParam("code") String code) {
//		// 0. 前端 google 登入的連結，按下去以後 送使用者到 google 頁面
//		
//		// 1. google 於 使用者同意授權以後，會打回來這支 api
//		// 使用者同意授權後，Google 會 redirect 到：
//		// 這個 code 就是 短效 授權碼 (Authorization Code)，只能使用一次，有效期很短（通常 30 秒內）。
//		System.out.println("Google 送來的 code : " + code);
//		
//		String res = GOOGLE_SERVICE.extractToken(code);
//		// 2. 後端 發一個 POST 給 Google -> RestClient
//		System.out.println(res);
//		
//		return "redirect:/front-end/index";
//	}
//	
//	
//
//}
//
//  