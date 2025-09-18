package com.auth; // 📦 套件：這支 Controller 負責「認證（Auth）」相關 API

import java.time.Duration; // ⏱️ 用來描述 Token 存活時間（TTL, Time-To-Live）

import org.springframework.http.HttpHeaders;        // 📮 用來設定 Response Header（例如 Set-Cookie）
import org.springframework.http.ResponseCookie;     // 🍪 Spring 封裝的 Cookie Builder，容易設定 HttpOnly/SameSite 等屬性
import org.springframework.http.ResponseEntity;    // 📦 回傳 HTTP 回應的便利類別（狀態碼 + Header + Body）
import org.springframework.security.authentication.AuthenticationManager;              // 🔐 Spring Security 的「驗證管理器」
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 🪪 裝「帳密」的 Authentication 實作（未驗證 → 交給 Provider）
import org.springframework.security.core.Authentication;                                // 🧾 表示「一個認證結果」的介面（principal/authorities/...）
import org.springframework.web.bind.annotation.PostMapping;  // 📮 對應 HTTP POST
import org.springframework.web.bind.annotation.RequestBody;   // 📦 將 JSON body 轉成 Java 物件
import org.springframework.web.bind.annotation.RequestMapping; // 📍 設定 Controller 的共同路徑前綴
import org.springframework.web.bind.annotation.RestController; // 🌐 這是一個 REST Controller（回傳 JSON）

import com.config.SecurityConfig; // ⚙️ 你的安全設定（這裡只拿常數：Cookie 名稱）
import com.properties.JwtProperties;
import com.security.jwt.JwtUtil;  // 🔏 你自訂的 JWT 工具類（簽發/驗證/解析）

import jakarta.validation.constraints.NotBlank; // ✅ 驗證請求欄位用（不可為空白）

//大部分實務專案都是「集中式 Auth 服務」：
		// 1. /api/auth/login
		// 2. /api/auth/logout
		// 3. /api/auth/refresh
	// 然後角色差異靠 UserDetails + GrantedAuthority 處理，
	// 不會拆成 /front-end/login、/back-end/login 各寫一份邏輯。
//唯一需要小心的點 -> 登入後的導向行為：不同角色可能要去不同頁面 → 這部分應該交給前端 JS 根據 role 做 redirect。

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
	
    private final AuthenticationManager am; // 🧠 「把 Authentication 交出去驗證」的入口（背後會串連到 AuthenticationProvider）
//    private final JwtProperties jwtProps;
    private final Duration MEM_ACCESS_TTL;
    private final Duration MEM_REFRESH_TTL;
    private final Duration EXHIB_ACCESS_TTL;
    private final Duration EXHIB_REFRESH_TTL;
    private final JwtUtil jwt;              // 🛠️ 簽發/驗證/解析 JWT 的工具


    public AuthRestController(AuthenticationManager am, JwtProperties jwtProps, JwtUtil jwt) {
        this.am = am;
        this.jwt = jwt;
        this.MEM_ACCESS_TTL = jwtProps.memAccessTtl();
        this.MEM_REFRESH_TTL = jwtProps.memRefreshTtl();
        this.EXHIB_ACCESS_TTL = jwtProps.exhibAccessTtl();
        this.EXHIB_REFRESH_TTL = jwtProps.exhibRefreshTtl();
    }

    // ===================== 登入 =====================
    @PostMapping("/login/member") // 📮 POST /api/auth/login：接帳密，成功後下發 JWT（Cookie 版）
    public ResponseEntity<?> loginMember(@RequestBody LoginReq req) {
        // 1) 呼叫 AuthenticationManager（平常例如表單登入是 UsernamePasswordAuthenticationFilter 自己呼叫 am 去做事，開發者不會手動碰到）做帳密驗證
        //    - 這裡建立「未驗證」的 UsernamePasswordAuthenticationToken（只裝 username/password）
        //    - 交給 AuthenticationManager.authenticate(...) 後，
        //      會由 DaoAuthenticationProvider 使用 UserDetailsService + PasswordEncoder 去比對密碼
        //    - 驗證成功 → 回傳「已驗證」的 Authentication（裡面有 principal/authorities），否則丟 AuthenticationException
        Authentication auth = am.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        // 2) 驗證成功 → 以「使用者身分」為 subject 簽發 Access/Refresh 兩顆 JWT
        //    - Access：用在每次請求做授權判斷（短效）
        //    - Refresh：專門用來換新 Access（長效 & 僅伺服器端驗證使用）
        
        String access  = jwt.generateAccess(auth.getName(), MEM_ACCESS_TTL);
        String refresh = jwt.generateRefresh(auth.getName(), MEM_REFRESH_TTL);
        // Authentication 型別可以是 String 或 UserDetails
        	// String -> 直接回傳字串
        	// UserDetails -> userDetails.getUsername()

        // 3) 把 Access Token 放進「HttpOnly Cookie」
        //    - HttpOnly：JS 不能讀，降低 XSS 外洩風險
        //    - Secure：只在 HTTPS 下傳送（本機開發如 http://localhost 可暫時設 false，正式一定要 true）
        //    - SameSite=Lax：一般瀏覽情境自動帶 Cookie；跨站表單/連結大多不帶，減少 CSRF 風險
        //    - Path=/ ：整站有效（登出清 Cookie 時，也要用同樣 Path 才清得到）
        //    - maxAge：存活時間（瀏覽器端生命週期）
        ResponseCookie accessCookie = ResponseCookie.from(SecurityConfig.MEM_ACCESS_COOKIE, access)
            .httpOnly(true)   // JS 讀不到 → 防 XSS 竊取
            .secure(false)     // 僅 HTTPS 請求可送出 ；本地開發可暫時 false，上線必須是 true -> localhost 可以通過 true, 但 127.0.0.1 不行
            						// Secure 主要是防止 Cookie 在 HTTP 傳輸時被竊聽（防中間人攻擊）。
            						// 基礎安全
            .sameSite("Lax")  // 決定 Cookie 是否能在「跨站請求」中被自動帶上。這就是 CSRF 攻擊的核心點。-> 補充：其實我們專案用 Strict 也沒差（同網域）
            						// Strict: Cookie 只在同站請求才會帶上
            							// 最嚴格的防護，幾乎消滅 CSRF，但也可能影響使用者體驗
									// Lax: Cookie 在大部分跨站情境 不會帶上，但有一個例外：如果是「安全的 GET 請求」（例如使用者點超連結、提交 <form method="GET">），Cookie 還是會帶上。
            							// 能防止大部分 CSRF（因為惡意網站通常需要 POST/PUT/DELETE），同時保留從外部連進來時能保持登入。
									// None: Cookie 總是會帶上，不論同站或跨站。
            							// 完全沒有防 CSRF 保護。必須搭配 Secure 使用（Chrome 要求），否則不允許設置。
            .path("/")        // Cookie 作用範圍（與清除時一致）
            .maxAge(MEM_ACCESS_TTL) // ⏰ 15 分鐘
            .build();

        // 4) 同理，Refresh Token 也放 Cookie，但更嚴（Strict）
        //    - SameSite=Strict：幾乎不會在跨站情境自動帶上，降低被 CSRF「被動刷新」的風險
        //    - 通常 Refresh 只在「同站的 /api/auth/refresh」時才會被用到
        ResponseCookie refreshCookie = ResponseCookie.from(SecurityConfig.MEM_REFRESH_COOKIE, refresh)
            .httpOnly(true)
            .secure(false)
            .sameSite("Strict") // 🔒 比 Lax 更嚴：避免第三方導流時夾帶刷新
            .path("/")
            .maxAge(MEM_REFRESH_TTL) // ⏰ 7 天
            .build();

        // 5) 回應：200 OK + 兩顆 Set-Cookie
        //    - ResponseEntity.header(HttpHeaders.SET_COOKIE, ...) 可呼叫多次 → 會加成兩個 Set-Cookie header（不會互蓋）
        //    - body 可以帶登入結果或使用者簡要資訊，此處僅示意 {"status":"ok"}
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(new LoginRes("ok"));
    }
    @PostMapping("/login/exhibitor")
    public ResponseEntity<?> loginExhibitor(@RequestBody LoginReq req) {
        Authentication auth = am.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        // 驗證成功
        
        String access  = jwt.generateAccess(auth.getName(), EXHIB_ACCESS_TTL);
        String refresh = jwt.generateRefresh(auth.getName(), EXHIB_REFRESH_TTL);

        ResponseCookie accessCookie = ResponseCookie.from(SecurityConfig.EXHIB_ACCESS_COOKIE, access)
            .httpOnly(true) 
            .secure(true)     
            .sameSite("Strict") 
            .path("/")
            .maxAge(EXHIB_ACCESS_TTL) // 10 分鐘
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from(SecurityConfig.EXHIB_REFRESH_COOKIE, refresh)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict") 
            .path("/")
            .maxAge(EXHIB_REFRESH_TTL) // 3 天
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(new LoginRes("ok"));
    }

    // ===================== 刷新 Token =====================
    
//    @PostMapping("/refresh") // 📮 POST /api/auth/refresh：用 Cookie 中的 Refresh 來換新 Access
//    public ResponseEntity<?> refresh(
//        // 🍪 直接從 Cookie 讀 Refresh Token
//        //    - required=false：如果沒帶 Cookie 不會 400，而是進方法後自行處理（回 401）
//        @CookieValue(name = SecurityConfig.REFRESH_COOKIE, required = false) String refresh
//    ) {
//        // A) 先做基本檢查：存在 / 簽章正確 / 未過期 / 類型為 refresh
//        //    - jwt.validate(...)：驗簽 + 時效
//        //    - jwt.isRefreshToken(...)：避免拿 Access 假裝 Refresh 來刷新
//        if (refresh == null || !jwt.validate(refresh) || !jwt.isRefreshToken(refresh)) {
//            // ⚠️ 401 Unauthorized：代表「尚未通過認證」，前端可導回登入或嘗試重新登入
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                                 .body(Map.of("error", "invalid_refresh"));
//        }
//
//        // B) 從合法的 Refresh Token 取出使用者身分（通常是 username/userId）
//        String username = jwt.getUsername(refresh);
//
//        // C) 重新簽發一顆「新的 Access Token」給前端
//        String newAccess = jwt.generateAccess(username, SecurityConfig.ACCESS_TTL);
//
//        // D) 以同名 Cookie 覆蓋（Set-Cookie）舊的 Access Cookie
//        ResponseCookie accessCookie = ResponseCookie.from(SecurityConfig.ACCESS_COOKIE, newAccess)
//            .httpOnly(true)
//            .secure(true)
//            .sameSite("Lax")
//            .path("/")
//            .maxAge(SecurityConfig.ACCESS_TTL)
//            .build();
//
//        // E) 回 200 並下發新的 Access Cookie
//        //    - 注意：Refresh Cookie 仍維持原有效期（看你策略是否同時滾動更新 Refresh）
//        return ResponseEntity.ok()
//            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
//            .body(Map.of("status", "refreshed"));
//    }

    // ===================== 登出 =====================
//    @PostMapping("/logout") // 📮 POST /api/auth/logout：語意上的「登出 API」
//    public ResponseEntity<?> logout() {
//        // 實際的「清 Cookie」動作不是在這裡做
//        // 👉 已在 SecurityConfig 的 http.logout(...) 中，註冊了 CookieClearingLogoutHandler
//        //    - 當請求命中 /api/auth/logout 時，由 LogoutFilter 依序呼叫該 Handler 清除 ACCESS/REFRESH Cookie
//        //    - 然後由你設定的 LogoutSuccessHandler 回應 204
//        // 這裡維持語意一致：回 204 No Content，前端據此清本地狀態/跳轉
//    	// .noContent() === ResponseEntity.status(HttpStatus.NO_CONTENT)
//        return ResponseEntity.noContent().build();
//    }

    // ===================== 輔助類：Request/Response DTO =====================
    // 在 Java 16+ 引入的 record 是一種特殊語法，幫你自動生成：
    	// private final 欄位
    	// 全參數建構子
    	// getter 方法（但形式是 fieldName() 而不是 getFieldName()）
    	// equals / hashCode / toString
    // 📦 LoginReq：接前端登入表單的 JSON（用 @NotBlank 確保不是空字串）
    public record LoginReq(@NotBlank String username, @NotBlank String password) {}
    // 📦 LoginRes：回應簡單狀態，實務上可帶 user profile/roles 等
    public record LoginRes(String status) {}
}
