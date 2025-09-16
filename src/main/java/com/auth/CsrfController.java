package com.auth;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {
    @GetMapping("/api/auth/csrf_token")
    public CsrfToken csrf(CsrfToken token) {
    	// 其實 cookie 下發 完全不用靠 return -> return 是給 js 想取得 token 用的。（我們其實沒有要使用） 
    	// Set-Cookie: XSRF-TOKEN=... 完全是 CsrfFilter + CookieCsrfTokenRepository 決定的
    	// 只要 request 進過 CsrfFilter，它就會檢查 token，有沒有就生成，然後交給 repository → 自動下 cookie。
    	// Controller 就算 void、或 return 別的 JSON，cookie 還是會下發。
    
        return token; // Spring Security 會自動注入
    }
}

// CsrfFilter：在 filter chain 裡檢查 & 放置 token。
// CookieCsrfTokenRepository：負責把 token 存放在 cookie。
// CsrfToken 類別：只是讓你在 Controller 層級能「看到」 token（不回傳也沒差，cookie 還是會種）。