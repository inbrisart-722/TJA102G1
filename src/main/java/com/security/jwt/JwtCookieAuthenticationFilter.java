//package com.security.jwt;
//
//import java.io.IOException;
//import java.util.Arrays;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.config.SecurityConfig;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
///**
// * JwtCookieAuthenticationFilter
// *
// * 角色與責任：
// * - 這是一個 Security Filter（繼承 OncePerRequestFilter，確保同一請求只執行一次）。
// * - 它不負責「登入」（帳密驗證、簽發 JWT），只負責「已帶 JWT 的請求」驗證與建構 Spring Security 的 Authentication。
// * - 工作流程：
// *   1) 從 Cookie（或你定義的位置）取出 Access Token。
// *   2) 驗證 Token（簽章、到期、issuer、audience…）。
// *   3) 解析出使用者識別（通常是 username / memberId）。
// *   4) 載入 UserDetails，建立 Authentication，放入 SecurityContext。
// *   5) 放行給後續 Filter；若沒有合法 JWT，就不放 Authentication，讓後面的授權流程自行處理（401 或導向登入）。
// *
// * 重要特性與注意事項：
// * - Filter 本身「不丟例外來做導頁」。未認證/權限不足的後續處理由 ExceptionTranslationFilter 搭配
// *   AuthenticationEntryPoint / AccessDeniedHandler 負責（在 SecurityConfig 裡設定）。
// * - 使用 Cookie 存 JWT 時，建議 Cookie 屬性：HttpOnly、Secure（https）、SameSite（依跨域需求為 Lax/None）。
// * - 若 Access Token 與 Refresh Token 分開，這個 Filter 通常只讀「Access Token」。
// * - 預設會對每個請求執行；若你的 /api/auth/login /api/auth/refresh 等端點不需要此驗證，可覆寫 shouldNotFilter() 排除。
// */
//@Component
//public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {
//
//    /**
//     * JwtUtil：你的 JWT 工具類，應提供：
//     * - validate(token): 驗簽、檢查 exp、nbf、aud/iss 等（必要時處理 clock skew）。
//     * - getUsername(token): 從 claims 取出 subject（或自訂欄位）。
//     *
//     * ⚠️ 安全建議：
//     * - 驗證時務必檢查簽章與到期；不要只做字串解碼。
//     * - 若系統有金鑰輪替，這裡要支援多把金鑰驗證。
//     */
//    private final JwtUtil jwt;
//
//    /**
//     * UserDetailsService：由 Spring Security 提供/你實作，負責：
//     * - 依 username 載入使用者與權限（roles/authorities）。
//     *
//     * 設計權衡：
//     * - 每次請求都 loadUserByUsername 會打 DB → 穩健但較耗資源。
//     * - 若把角色直接放進 JWT，可減少 DB hit，但要面對權限變更延遲與撤銷難題。
//     */
//    private final UserDetailsService uds;
//
//    public JwtCookieAuthenticationFilter(JwtUtil jwt, UserDetailsService uds) {
//        this.jwt = jwt;
//        this.uds = uds;
//    }
//
//    /**
//     * Filter 核心邏輯：
//     * - 若驗證成功：建立 Authentication → 放入 SecurityContext → 放行。
//     * - 若驗證失敗或沒有 Token：不要動 SecurityContext（保持匿名），直接放行，讓授權層與 ExceptionHandling 決定行為。
//     *
//     * 注意：
//     * - 請勿在這裡直接送 401 或 redirect；會破壞統一的例外處理流程。
//     * - OncePerRequestFilter 已處理同一請求重入；不用再手刻防重入邏輯。
//     */
//    @Override
//    protected void doFilterInternal(HttpServletRequest req,
//                                    HttpServletResponse res,
//                                    FilterChain chain) throws ServletException, IOException {
//
//        // 1) 從 Cookie 找出 Access Token
//        //    - SecurityConfig.ACCESS_COOKIE：建議是一個常數名稱，例如 "access_token"
//        //    - 若你的系統也支援 Authorization: Bearer ...，可在此加入 header fallback。
//        String token = extractCookie(req.getCookies(), SecurityConfig.ACCESS_COOKIE);
//
//        // 2) 確認：有 token 且目前尚未建立 Authentication（避免重複覆蓋既有身份）
//        //    - SecurityContextHolder 以 ThreadLocal 保存「本請求」的認證資訊。
//        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            // 3) 驗證 Token 是否有效
//            //    - validate() 應做：簽章、到期、黑名單/撤銷（若你有實作）、必要的標準 claims 檢查
//            if (jwt.validate(token)) {
//
//                // 4) 從 Token 解析出使用者名稱（或唯一識別）
//                //    - 常見用 sub 作為 username。
//                //    - 若你的系統以 memberId 為主，可在此回收成 username 或在 UserDetails 中裝載 memberId。
//                String username = jwt.getUsername(token);
//
//                // 5) 載入使用者完整資訊 (含角色/權限)
//                //    - 預設透過 DB 或快取查詢。
//                //    - 若你把權限放在 JWT，可考慮跳過 DB 查詢，但要思考權限變更的延遲與撤銷。
//                UserDetails user = uds.loadUserByUsername(username);
//
//                // 6) 建立 Authentication 物件，代表「這個 request 的使用者」
//                //    - 這裡 credentials 設為 null（因為我們不再持有密碼）。
//                //    - getAuthorities() 提供授權層使用的角色/權限資訊。
//                UsernamePasswordAuthenticationToken auth =
//                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//
//                // 7) 加上請求的細節 (例如 IP, Session ID)
//                //    - 某些審計或存取決策可能用得上（配合 ConcurrentSessionControl/會話管理）。
//                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
//
//                // 8) 把 Authentication 存進 SecurityContext
//                //    - 從這一刻起，後續的 Controller/Service 可用 @AuthenticationPrincipal 或 SecurityContext 取得使用者。
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//            // else：validate 失敗 → 不設 Authentication，交由後續授權與異常處理（401 / redirect）負責。
//        }
//
//        // 9) 無論是否成功，都必須放行，讓請求繼續走
//        //    - 這是 Filter 的基本約定；是否「允許匿名進入該路徑」由授權規則決定。
//        //    - 若路徑需要認證，且此時 SecurityContext 沒有 Authentication，
//        //      FilterSecurityInterceptor 會拋 AuthenticationException，
//        //      之後由 ExceptionTranslationFilter 轉交 AuthenticationEntryPoint → 回 401 或導向登入頁。
//        
//        // 就算沒成功，直接放行，讓後面的 Security Filter, 特別是 ExceptionTranslationFilter 判斷「這個請求需要認證，但 SecurityContext 是空的」 -> 觸發「未認證流程」
//    	// SecurityConfig -> AuthenticationEntryPoint
//        chain.doFilter(req, res);
//    }
//
//    /**
//     * 從 Cookie 陣列中取出指定名稱的 Cookie 值
//     *
//     * 設計建議：
//     * - Cookie 屬性應在發送時設定（Set-Cookie）：HttpOnly, Secure, SameSite（依跨域策略）。
//     * - 如需支援多網域/子網域，留意 Cookie Domain 與 Path；盡量最小化作用範圍。
//     * - 若要支援 Bearer header，可在本方法外另寫 extractBearer(req) 做備援。
//     */
//    private String extractCookie(Cookie[] cookies, String name) {
//        if (cookies == null) return null;
//
//        return Arrays.stream(cookies)
//                .filter(c -> name.equals(c.getName()))
//                .map(Cookie::getValue)
//                .findFirst()
//                .orElse(null);
//    }
//
//    /*
//     * 進階：若某些路徑不需要執行此 Filter（例如 /api/auth/login, /api/auth/refresh, 靜態資源），
//     * 可覆寫 OncePerRequestFilter.shouldNotFilter(HttpServletRequest request) 回傳 true 來跳過：
//     *
//     * @Override
//     * protected boolean shouldNotFilter(HttpServletRequest request) {
//     *     String uri = request.getRequestURI();
//     *     return uri.startsWith("/api/auth/") || uri.startsWith("/css/") || uri.startsWith("/js/");
//     * }
//     *
//     * 這樣可少做無謂的 JWT 解析與驗證，提高效能並避免在登入/刷新端點干擾流程。
//     */
//
//    /*
//     * 進階：Filter 在鏈中的擺放位置
//     * - 一般會在 SecurityConfig 裡用：
//     *   http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//     *   讓它早於授權判斷執行（以便設好 Authentication）。
//     * - 確保位於 ExceptionTranslationFilter 之前（通常 addFilterBefore UPAF 足夠）。
//     */
//
//    /*
//     * 進階：錯誤處理與審計
//     * - 若 validate() 時拋出例外，建議在這裡捕捉並記錄（log warn/error），但仍放行讓統一異常流程接手。
//     * - 可在這裡做基本的 metrics/Tracing（例如計數驗證失敗次數、來源 IP），協助風控或偵錯。
//     */
//
//    /*
//     * 進階：效能與快取策略
//     * - 頻繁地 uds.loadUserByUsername() 可能成為瓶頸。
//     * - 可考慮把使用者權限快取在 JWT（權衡撤銷與變更同步），或在 uds 層加上快取（Caffeine/Redis）。
//     * - 若啟用 refresh rotation，Access Token 存活時間可設很短（例如 5–15 分鐘）以降低風險。
//     */
//}


///
///
///
// 以下放的是 自動更新 cookie 版本（refresh -> access)
///
///
///

package com.security.jwt;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.config.SecurityConfig;
import com.properties.JwtProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
//    private final JwtProperties jwtProps;
    private final Duration MEM_ACCESS_TTL;
    private final Duration EXHIB_ACCESS_TTL;
    private final UserDetailsService uds;

    public JwtCookieAuthenticationFilter(JwtUtil jwt, JwtProperties jwtProps, UserDetailsService uds) {
        this.jwt = jwt;
        this.uds = uds;
        this.MEM_ACCESS_TTL = jwtProps.memAccessTtl();
        this.EXHIB_ACCESS_TTL = jwtProps.exhibAccessTtl();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

    	// 在 Spring Security 的設計裡
    	// SecurityContextHolder.getContext().setAuthentication(auth) 只能有一個 Authentication。
    	
    	String path = req.getRequestURI();
    	if (path.startsWith("/api/auth/login")) {
    	    chain.doFilter(req, res);
    	    return;
    	}
    	
    	if(req.getRequestURI().startsWith("/front-end") || req.getRequestURI().startsWith("/api/front-end")) {
        // 1) 嘗試處理會員 Token
    		handleTokenFlow(req, res,
                SecurityConfig.MEM_ACCESS_COOKIE,
                SecurityConfig.MEM_REFRESH_COOKIE,
                MEM_ACCESS_TTL);
    		chain.doFilter(req, res);
    		return;
    	}
    	else {
        // 2) 嘗試處理展商 Token
    		handleTokenFlow(req, res,
                SecurityConfig.EXHIB_ACCESS_COOKIE,
                SecurityConfig.EXHIB_REFRESH_COOKIE,
                EXHIB_ACCESS_TTL);

    		chain.doFilter(req, res);
    		return;
    	}
    }

    // =================== 處理 Token 流程 ===================
    private void handleTokenFlow(HttpServletRequest req,
                                 HttpServletResponse res,
                                 String accessCookieName,
                                 String refreshCookieName,
                                 Duration accessTtl) {
        String accessToken = extractCookie(req.getCookies(), accessCookieName);

        if (accessToken != null && jwt.validate(accessToken)) {
            // --- Access Token 有效 ---
            authenticateUser(accessToken, req);

        } else {
            // --- Access Token 無效或過期 → 嘗試 Refresh ---
            String refreshToken = extractCookie(req.getCookies(), refreshCookieName);

            if (refreshToken != null && jwt.validate(refreshToken) && jwt.isRefreshToken(refreshToken)) {
                String username = jwt.getUsername(refreshToken);

                // 1. 簽發新的 Access Token
                String newAccess = jwt.generateAccess(username, accessTtl);

                // 2. 更新 Access Token Cookie
                ResponseCookie newCookie = ResponseCookie.from(accessCookieName, newAccess)
                        .httpOnly(true).secure(false) // ⚠️ 測試環境 false，正式要 true
                        .sameSite("Lax").path("/")
                        .maxAge(accessTtl)
                        .build();
                res.addHeader("Set-Cookie", newCookie.toString());

                // 3. 直接用新的 Access Token 完成身份驗證
                authenticateUser(newAccess, req);
            }
        }
    }

    // =================== 小工具：取 Cookie 值 ===================
    private String extractCookie(Cookie[] cookies, String name) {
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    // =================== 小工具：Token → Authentication ===================
    private void authenticateUser(String token, HttpServletRequest req) {
        String username = jwt.getUsername(token);
        UserDetails user = uds.loadUserByUsername(username);

        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
