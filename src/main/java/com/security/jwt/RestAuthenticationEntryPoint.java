package com.security.jwt; // 📦 package 定義，屬於 JWT 安全相關工具類別

import java.io.IOException;   // 處理輸出時可能丟出的 IO 例外
import java.util.Map;         // 用於建立 key-value map (這裡用來回傳 JSON)

import org.springframework.http.MediaType;                       // 定義回應的 Content-Type，例如 JSON
import org.springframework.security.core.AuthenticationException; // Spring Security 的驗證例外
import org.springframework.security.web.AuthenticationEntryPoint; // 🔑 Security 的介面，處理「未授權/未驗證」的進入點
import org.springframework.stereotype.Component;                 // Spring 註解，表示這是一個可被管理的 Bean

import com.fasterxml.jackson.databind.ObjectMapper; // Jackson：用於物件與 JSON 的序列化/反序列化

import jakarta.servlet.http.HttpServletRequest;  // Servlet API：代表一個 HTTP 請求
import jakarta.servlet.http.HttpServletResponse; // Servlet API：代表一個 HTTP 回應

//🔑 它的角色是什麼？
	//在 Spring Security + JWT 中，當有這些情況時會觸發：
	//請求帶了 無效的 JWT。
	//請求沒有帶 Token。
	//使用者尚未登入卻嘗試存取受保護的資源。
	//預設情況下，Spring Security 會導向 登入頁面 (HTML redirect)，但對於 REST API / 前後端分離專案來說，這樣不合適。
		//👉 所以我們實作 RestAuthenticationEntryPoint，改成回傳 JSON 格式的錯誤，給前端處理。

// 非 JWT 專屬
// Spring Security 預設行為: redirect 到 /login （只適合傳統表單登入／SSR）
// 自訂後：回 JSON (401) （適合 API／CSR）
// 這個類別只會用在「API 沒登入」的場景，如果是 SSR request，就不會用這裡，而是 redirect 到 /login 即可。
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // ObjectMapper：Jackson 的核心類，負責把 Java 物件轉換成 JSON
    private final ObjectMapper om = new ObjectMapper(); // 也可採建構子注入等注入方式不用自己 new

    // 📌 當使用者「未登入」或「Token 驗證失敗」時，Spring Security 會呼叫這個方法
    @Override
    public void commence(HttpServletRequest req,   // 當前請求 (例如: 哪個 API 被打)
                         HttpServletResponse res, // 回應物件，用來回傳錯誤訊息
                         AuthenticationException ex // 具體的驗證錯誤 (例如：憑證錯誤、帳號不存在)
    ) throws IOException {
        // 設定 HTTP 狀態碼為 401 Unauthorized
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 設定回應的 Content-Type 為 JSON 格式
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 用 ObjectMapper 輸出一個 JSON，內容包含 error 與 message 欄位
        om.writeValue(res.getWriter(), Map.of(
            "error", "unauthorized",   // 固定回傳的錯誤類型
            "message", ex.getMessage() // 例外訊息，可能是「Bad credentials」等
        ));
    }
}
