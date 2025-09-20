//package com.eventra.member.github;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequestMapping("/front-end/github")
//public class GitHubRestController {
//
//	private final GitHubService GITHUB_SERVICE;
//	
//	public GitHubRestController(GitHubService githubService) {
//		this.GITHUB_SERVICE = githubService;
//	}
//	
//	// https://docs.github.com/en/apps/creating-github-apps/authenticating-with-a-github-app/generating-a-user-access-token-for-a-github-app
//	@GetMapping("/callback")
//	public String callback(@RequestParam("code") String code, @RequestParam("state") String state) {
//		
//		// 0. 前端送出之網址
//		
////		https://github.com/login/oauth/authorize
////			?client_id=Ov23liwsF9wEhG1QHznF
////			&redirect_uri=http://localhost:8088/front-end/github/callback
////			&scope=user:email
////			&state=RANDOM_STRING
//		
//		// code: 授權碼 (Authorization Code)，拿去換 access_token 的憑證
//			// 一次性使用（用過就失效）
//			// 有時效性（1 min 內）
//			// client-to-server get 打回來，需避免 access_token 暴露在瀏覽器網址列
//		// state: 隨機字串防止 CSRF
//			// 記得檢查是否與送出時的一致（否則代表此 callback 不是我們該處理的，可能是惡意攻擊）
//		// scope: 權限範圍（決定我們能跟使用者要哪些資料）
//			// read:user -> 讀取基本資料（id, login, name, avatar）
//			// user:email -> 讀取 email
//			// repo -> 讀取/管理使用者的 repo
//			// gist -> 建立 gist
//		
//		System.out.println("code: " +code);
//		System.out.println("state: " +state);
//		
//		GITHUB_SERVICE.validateState(state);
//		GITHUB_SERVICE.extractToken(code);
//		System.out.println("帥哥");
//		
//		return "redirect:/front-end/index";
//	}
//}
