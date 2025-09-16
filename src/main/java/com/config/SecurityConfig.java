package com.config; // 專案內的設定類別都放在 com.config 套件

import org.springframework.context.annotation.Bean; // 宣告 Spring Bean 用
import org.springframework.context.annotation.Configuration; // 表示這是一個設定類別
import org.springframework.security.authentication.AuthenticationManager; // 驗證帳密的核心元件
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // 使用資料庫帳號密碼的驗證提供者
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // 取得 AuthenticationManager 用
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // 建構 SecurityFilterChain 的 DSL
import org.springframework.security.config.http.SessionCreationPolicy; // 設定 Session 策略
import org.springframework.security.core.userdetails.UserDetailsService; // 載入使用者（你實作的 CustomUserDetailsService）
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // BCrypt 密碼雜湊
import org.springframework.security.crypto.password.PasswordEncoder; // 密碼編碼介面
import org.springframework.security.web.SecurityFilterChain; // Spring Security 的過濾鏈
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint; // 未登入導頁用（給頁面）
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 參考定位自訂 Filter 的相對位置
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler; // 登出時清 Cookie 用
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // 比對路徑/HTTP 方法用
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import com.security.jwt.JwtCookieAuthenticationFilter; // 你自訂：從 HttpOnly Cookie 讀取 JWT 的 Filter
import com.security.jwt.RestAuthenticationEntryPoint; // 你自訂：API 端未認證時回 401 JSON

@Configuration // 告訴 Spring：這是一個設定類別，會產生 Bean
public class SecurityConfig {

    // ===================== 常數：Cookie 名稱 =====================
    public static final String MEM_ACCESS_COOKIE = "MEM_ACCESS_TOKEN";   // Access Token 放這顆 Cookie（HttpOnly）
    public static final String MEM_REFRESH_COOKIE = "MEM_REFRESH_TOKEN"; // Refresh Token 放這顆 Cookie（HttpOnly）
    public static final String EXHIB_ACCESS_COOKIE = "EXHIB_ACCESS_TOKEN";   // Access Token 放這顆 Cookie（HttpOnly）
    public static final String EXHIB_REFRESH_COOKIE = "EXHIB_REFRESH_TOKEN"; // Refresh Token 放這顆 Cookie（HttpOnly）
    
    // ⏱️ Token 存活時間（TTL）：Access Token 短效，Refresh Token 長效
    // java.time.Duration (Java 8+) not Spring or Spring Security-specific
//    public static final Duration MEM_ACCESS_TTL  = Duration.ofMinutes(15); // ⏳ Access 15 分鐘：降低外洩風險
//    public static final Duration MEM_REFRESH_TTL = Duration.ofDays(7);     // 🗓️ Refresh 7 天：用來換新 Access
//    public static final Duration EXHIB_ACCESS_TTL  = Duration.ofMinutes(10);
//    public static final Duration EXHIB_REFRESH_TTL = Duration.ofDays(3);

    // ===================== Bean：PasswordEncoder =====================
    @Bean 
    // 對外提供一顆 PasswordEncoder Bean，給註冊/改密碼/驗證共用
    // Service 層處理註冊需一併統一使用 Spring Security 提供的 BCryptPasswordEncoder
    // -> 之後登入時，Spring Security 的 DaoAuthenticationProvider 會自動用一個 PasswordEncoder 驗證密碼。就不用自己實作比對 raw vs hash...
    
    // public void registerUser(String username, String rawPassword) {
    // 		repo.save(new User().setUsername(username).setPassword(passwordEncoder.encode(rawPassword))); // 用 BCrypt 雜湊後存入 DB
    // }
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 使用 BCrypt（有鹽、強度足夠）
    }

    // ===================== Bean：DaoAuthenticationProvider =====================
    @Bean 
    
    // 提供 DaoAuthenticationProvider，連接 UserDetailsService + PasswordEncoder
    // Spring Security 內建的一個 AuthenticationProvider -> 透過 DAO（UserDetailsService）去 DB 查使用者
    // 用「帳號 + 密碼」的方式驗證使用者。
    	// 1. 取得使用者輸入的帳號/密碼
    	// 2. 呼叫 UserDetailsService 載入使用者資料（通常是去 DB 查）
    	// 3. 用 PasswordEncoder 驗證輸入密碼是否與 DB 雜湊密碼相符。
    	// 4-1. 驗證成功 -> 建立 UsernamePasswordAuthenticationToken -> 放進 "SecurityContext"
    	// 4-2. 驗證失敗 -> 丟出 BadCredentialsException 或其他驗證錯誤
    
    // 與 daoAuthenticationProvider 沒關係，手動設計 Bean (他只有單參數 constructor 吃 encoder)
    DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService uds, PasswordEncoder encoder) {
        var p = new DaoAuthenticationProvider(); // 建立 Provider 實例
        p.setUserDetailsService(uds);
        // 指定載入使用者的服務（你實作的 某 @Service implements UserDetailsService 覆寫 loadUserByUsername method），
        // 並且讓 SpringBootApplication（Spring Boot的話） 掃到
        // 其實概念上 @Service 也可以在 SecurityConfig 內部 new 一個匿名 UserDetailsService，但這樣會很亂，維護性差。
        p.setPasswordEncoder(encoder);           // 指定密碼比對用的編碼器（BCrypt）
        return p;                                // 交給 Spring 管理
    }
    // ===================== Authentication =====================
    // Authentication 在 Spring Security 用來代表使用者的身份狀態 -> 目前這個請求的使用者身份（Authentication）
    // 未驗證前：可能只裝了 username + password（憑證，還沒確認真偽）
    // 驗證後：會裝滿使用者資訊（UserDetails）、權限（roles）、是否已驗證
    // -> 想像成 HttpSession 的「使用者身份卡」，但比它更抽象、更泛用
    
    // ===================== UsernamePasswordAuthenticationToken =====================
    // 它是 Spring Security 內建的一個 Authentication 實作類，專門代表「使用者用 username + password 登入」這種情境，無需自己實作。
    // 登入請求階段：包裝使用者輸入的帳號／密碼
    // UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, rawPassword);
    	// 因為登入請求時，Spring Security 需要一個統一的包裝，才能交給驗證器處理。（此時只是單純的容器作用）
    // 登入成功階段：包裝經過驗證後的使用者資訊（UserDetails + 角色／權限）
    // Authentication auth = authenticationManager.authenticate(authReq);
    	// 既可以是 自動行為（用 Spring Security 的內建流程），
    	// 也可以是 手動行為（自己寫 filter / controller 登入）。
    
    // 1. 使用者輸入帳密 -> Spring Security new UsernamePasswordAuthenticationToken(username, rawPassword) // authenticated = false
    // 2. token 丟給 Authentication authResult = AuthenticationManager.authenticate(); 
    	// Spring Security 的「總控中心」AuthenticationManager 會找適合的 AuthenticationProvider 來處理
    // 3. 例如若為 DaoAuthenticationProvider 會
    	// 3-1. UserDetailsService 查使用者
    	// 3-2. PasswordEncoder 比對密碼
    // 4. 假設成功，回傳已驗證成功的 Authentication（Authentication 物件為 immutable -> 所以是產生一個新的 Authentication Object 回傳）
    	// pricipal: userDetails (包含 username ,password, enabled, etc.)
    	// credentials = null (密碼不用再保存）
    	// authorities = 使用者角色／權限
    	// authenticated = true
    // 5. 假設失敗，不會回傳 Authentication，而是直接丟出例外 -> AuthenticationException 的子類別
    	// try { Authentication authResult = authenticationManager.authenticate(authReq); } catch (AuthenticationException ex) {}
    
    // 登入前 → UsernamePasswordAuthenticationToken
    	// principal = username (字串)
    	// credentials = raw password (字串)
    	// authorities = null
    	// authenticated = false

    // 登入後 → 新的 UsernamePasswordAuthenticationToken
    	// principal = UserDetails (完整使用者資訊：username, encodedPassword, enabled, etc.)
    	// credentials = null（安全考量，不再保存明文）
    	// authorities = 使用者角色/權限
    	// authenticated = true
    	
    // ===================== Authentication-related Exceptions =====================
    // BadCredentialsException: 帳號或密碼錯誤
    // DisabledException: 帳號被停用
    // LockedException: 帳號被鎖定
    // AccountExpiredException: 帳號過期
    // CredentialsExpiredException: 密碼過期
    
    // ===================== Security Context =====================
    // Spring Security 的全域儲存區，用來存放「目前這個請求的使用者身份（Authentication）」。
    // 每個 request thread 都會綁一個 SecurityContext，而 SecurityContext 又存放在 SecurityContextHolder 裡。
    // 登入成功：把使用者資訊放進去
    // 之後的請求：隨時可以透過 SecurityContextHolder.getContext().getAuthentication() 取得目前使用者 -> 就像一個全域 session，但不需要你自己管理。
    // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    // String username = auth.getName();
    // Collection<? extends GrantedAuthority> roles = auth.getAuthorities(); // Authorities = 使用者的身份／權限 => 像是 ROLE_USER, ROLE_ADMIN ...
    

    // ===================== Bean：AuthenticationManager =====================
    @Bean 
    // 從 AuthenticationConfiguration 取出全域的 AuthenticationManager
    
    // 在 Spring Security 內部（像表單登入流程），它本來就會自動拿到 AuthenticationManager。
    	// → 所以如果你只用 formLogin()，完全不需要自己宣告。
    	// 但是在 你自己的程式碼（例如 REST API / JWT 登入 Controller, Service）需要自己調用，就必須要手動宣告一顆 Bean：
    
    // AuthenticationConfiguration
    	// Spring Security 5.7+， 專門幫助我們取得 Spring 自動組裝好的 AuthenticationManager
    	// 它本身是一個 @Configuration，裡面知道有哪些 AuthenticationProvider, UserDetailsService, PasswordEncoder，會幫忙組合起來
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager(); // 供登入 API 調用 authenticate()
    }

    // 自訂 LogoutHandler 分流，不同路徑清理不同 token
    @Bean
    public LogoutHandler customLogoutHandler() {
        return (request, response, authentication) -> {
            String uri = request.getRequestURI();

            if (uri.contains("/member")) {
                // 清除會員 Token
                new CookieClearingLogoutHandler(
                    SecurityConfig.MEM_ACCESS_COOKIE,
                    SecurityConfig.MEM_REFRESH_COOKIE
                ).logout(request, response, authentication);

            } else if (uri.contains("/exhibitor")) {
                // 清除展商 Token
                new CookieClearingLogoutHandler(
                    SecurityConfig.EXHIB_ACCESS_COOKIE,
                    SecurityConfig.EXHIB_REFRESH_COOKIE
                ).logout(request, response, authentication);
            }
        };
    }

    // ===================== 核心：安全過濾鏈 =====================
    @Bean
    // 建立並回傳 SecurityFilterChain（整個安全規則從這裡長出來）
    // SecurityFilterChain 裡面每個東西都是一個 Filter，基本上就是一個 List<Filter>
    	// 當請求進來時，Spring Security 會看
    		// 1. 這個請求符合哪條 SecurityFilterChain（可以定義多條，然後用 @Order 排順序）
    		// 2. 一旦匹配，請求就會依序通過那條 chain 中的所有 Filter
    public SecurityFilterChain filterChain(
            HttpSecurity http,                            // Spring 提供的 DSL（Domain-Specific Language) 入口 <-> GPL (General-Purpose Language）
            JwtCookieAuthenticationFilter jwtFilter,      // 你自訂的 JWT Cookie 驗證 Filter（從 Cookie 建 Authentication）
            RestAuthenticationEntryPoint restEntryPoint,  // 你自訂的 API 401 回應器
            DaoAuthenticationProvider daoAuthProvider     // ⚠ 修正重點：把 Bean 注入進來，而不是呼叫方法傳 null
    ) throws Exception {

        // ========= CSRF：開啟，並用 Cookie 供前端取得 Token =========
    	// Spring Security 為了防止 CSRF 攻擊，需要隨機產生一個 token，並且在「用戶端與伺服器」之間保持一致，這樣才能檢查請求是否合法
    	// 但那個 CSRF token 要存在哪？ -> 1) session 2) cookie 3) 自訂 header
    	// CsrfTokenRepository 為 Spring 提供的「儲存策略」介面 -> 1) generate 2) save 3) load 於內部被定義
    		// CookieCsrfTokenRepository 是 Spring Security 內建的實作類，stateless unlike HttpSessionCsrfTokenRepository, 顧名思義用 Cookie 來儲存 CSRF Token
    			// 1. 伺服器回應時，產生一顆 Cookie（預設名字 XSRF-TOKEN）。
    			// 2. 前端 JavaScript 可以讀這顆 Cookie，把值放到 X-XSRF-TOKEN Header。
    			// 3. 下次請求時，Spring 會比對 Cookie 與 Header 的值是否一致。
    			// ****** 用 「Cookie 裡的 Token vs Header 裡的 Token 是否一致」 來判斷請求是不是合法。 ******
    		// 👉 這樣就能確保：攻擊者就算能偽造請求（帶上 Session/JWT Cookie），卻無法帶上正確的 CSRF Token。
    	
    	// 其他還有 HttpSessionCsrfTokenRepository... 甚至也可以自己實作策略
    	
//    	  http.csrf(csrf -> csrf.disable()); 
        http.csrf(csrf -> csrf
        		// CSRF 本質：「讓使用者在不知情的情況下，發送一個 有副作用的請求」
        			// GET 不需要 CSRF Token（有時不會過 CSRF filter 就不會塞進去 cookie -> CookieCsrfTokenRepository）
        		// 現實情況：
        			// 有些開發者偷懶，把「刪除 / 更新」寫成 GET（例如 /delete?id=1）。
        			// 這時候就可能被 CSRF 利用。
        			// 所以資安最佳實踐是：不要用 GET 來做會改變狀態的操作。
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) 
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                	// 取消 XOR
                // 1) 產生一顆「非 HttpOnly」的 XSRF-TOKEN Cookie，2) HttpOnlyFalse -> JS 可讀取後放到 X-XSRF-TOKEN header
                	// 即使 XSRF-TOKEN 是 non-HttpOnly，攻擊者的惡意 JS 讀不到，因為：
                		// 攻擊者只能在「他控制的 domain」注入 JS，讀不到你網站 domain 的 Cookie（同源政策）。
                		// 所以只有「你自己前端的 JS」能讀到這顆 Token。
                	// CookieCsrfTokenRepository 是 Spring 提供的一種 Token 儲存策略（會自動在回應中加一顆 Cookie, 名字通常是 "XSRF-TOKEN"）
                
                .ignoringRequestMatchers( // 某些端點（登入/刷新/登出）可視需求略過 CSRF 檢查
                		// Ant -> Apache Ant 比對檔案路徑用
                		// Ant Path Matching -> *, **, ?
                        new AntPathRequestMatcher("/api/auth/login/*", "POST"),   // 登入：使用者「尚未登入」，根本沒有有效的 session/ JWT 可以被攻擊者濫用
//                        new AntPathRequestMatcher("/api/auth/refresh", "POST"), // 刷新 token： /refresh 只是讓 token 續命，等於「幫用戶自己延長 session」。攻擊者也拿不到回傳資訊，就算同網域我們也能設計 HttpOnly
                        // 很多實務系統選擇把 refresh token 放在 HttpOnly Cookie 裡。因為 refresh token 的唯一用途就是「由瀏覽器自動帶上 → server 端驗證 → 換新 access token」，前端程式碼根本不需要直接讀取它。
                        new AntPathRequestMatcher("/api/auth/logout/*", "POST"),   // 登出：清 Cookie，攻擊者如果偽造 logout, 頂多只是讓使用者被迫登出
                        new AntPathRequestMatcher("/api/front-end/order/ECPay/ReturnURL"),
                        new AntPathRequestMatcher("/front-end/ECPay/*")
                        
                        
                )
        );

        // ========= Session：完全無狀態（靠 JWT，不用 HttpSession） =========
        http.sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // API 請求用 JWT，頁面請求需要的時候還是能建 session
                // 也有 SessionCreationPolicy.STATELESS 等完全不採用 Session 的狀態，或 ALWAYS
                
                // ALWAYS
                	// 幾乎沒必要用，用 IF_REQUIRED 即可
                	// 每次請求都會建立 HttpSession（就算沒必要）。
                	// 結果：系統永遠有 session，負擔大、也不符合 stateless API 精神。
                // IF_REQUIRED
                	// 只有在需要時才建立 Session。
                	// 適合「混合型」（SSR + CSR 各半等等）。
                // NEVER
                	// 偏進階情境。Spring Security 自己不創建 Session，但如果應用程式其他地方已經有 Session，它會用。
                // STATELESS
                	// 永遠不使用 Session，適合*純* REST API + JWT 架構
                	// SecurityContext 每次都要靠 Token 來重建
        );

        // ========= 未認證行為：API 回 401 JSON；頁面導 /login =========
        // defaultAuthenticationEntryPointFor -> 預設的「未登入」進入點設定（針對特定路徑），或說指定「對某些 URL Pattern，如果沒登入，該怎麼處理？」	
        	// .defaultAuthenticationEntryPointFor(entryPoint, requestMatcher) // 未登入處理器、匹配請求規則
        	// 匹配邏輯是「先比對，第一個符合的就用」 -> 愈精確、範圍愈小，應該要寫在前面, 反之寫在後面
        	
        // exceptionHandling -> 例外處理 -> 配合 Spring Security 的 Filter Chain 如果認證或授權失敗，框架會丟出 Security 相關例外
        // http.exceptionHandling -> 就是告訴 Spring Security: 「當驗證／授權失敗時，要用什麼策略回應？」
        	// AuthenticationEntryPoint: 使用者「沒登入」卻存取受保護資源
        	// AccessDeniesHandler: 使用者「已登入」但權限不足
        
        // ExceptionTranslationFilter 是 Spring Security Filter Chain 裡的其中一個 Filter
        // 專門負責「攔住 AuthenticationException 或 AccessDeniedException」這兩種安全相關錯誤
        	// 走到 filter chain 最後一關的 FilterSecurityInterceptor，如果此路徑要驗證但 Security Context 中又沒東西，就會丟例外給 ExceptionTranslationFilter 處理
        // 發生例外時，此 Filter 不會直接丟給瀏覽器，而是丟給 AuthenticationEntryPoint 或 AccessDeniedHandler（把例外丟到正確的處理器去）
        	// 概念上來說，AuthenticationEntryPoint 處理「未認證」，AccessDeniedHandler 處理「權限不足」，兩者都是 handler !!
        http.exceptionHandling(ex -> ex
        		// authentication
                .defaultAuthenticationEntryPointFor( // 對 /api/** 的請求
                        restEntryPoint,              // 使用自訂 entry point → 回 401 + JSON
                        new AntPathRequestMatcher("/api/**")
                )
                .defaultAuthenticationEntryPointFor( // 對其他（視為頁面）請求
                		((req, res, authEx) -> {
                			String target = "/front-end/login?redirect=" + req.getRequestURI();
                			res.sendRedirect(target);
                		}),
                        new AntPathRequestMatcher("/front-end/**")
                )
                .defaultAuthenticationEntryPointFor(
                		((req, res, authEx) -> {
                			String target = "/back-end/exhibitor/exhibitor_login?redirect=" + req.getRequestURI();
                			res.sendRedirect(target);
                		}),
                		new AntPathRequestMatcher("/back-end/**")
                )
                .defaultAuthenticationEntryPointFor(
                		new LoginUrlAuthenticationEntryPoint("/platform/login"),
                		new AntPathRequestMatcher("/platform/**")
                )
                // authorization
                .accessDeniedHandler((req, res, exception) -> {
                	String uri = req.getRequestURI();
                	if(uri.startsWith("/back-end/")) {
                		res.sendRedirect("/back-end/exhibitor/exhibitor_login");
                	}
                })
                
                // 如果需要 authorization 還要另外設定，目前的 authentication 也還不足夠
                	// 要配合 authorizeHttpRequests 設定 .hasRole .hasAuthority 的頁面才會觸發部分角色沒有權限
                // .accessDeniedHandler((req, res, exx) -> {
                	// res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                	// res.setContentType("application/json");
                	// res.getWriter().write("{\"error\": \"forbidden\", \"message\": \"" + exx.getMessage() + "\"}");
                // }
        );

        /* exceptionHandling 本身不會鎖住頁面，它只決定「當驗證失敗時，要回什麼」 */
        /* authorizedHttpRequest() 才是負責決定「哪些路徑要經過認證」 */
        // 不會有「邏輯矛盾」，因為 authorizeHttpRequests 決定要不要驗證，只有「要驗證但沒通過」才會進 exceptionHandling。
        // 所以就算你 exceptionHandling 裡面寫了 /exhibitions/**，但 authorizeHttpRequests 放行了，它也根本不會被觸發。
        // 真正要保持一致的是：authorizeHttpRequests 的規則要跟你的 API/頁面設計一致，exceptionHandling 只是 fallback。
        
        // ========= 授權規則：哪些開放、哪些要登入 =========
        http.authorizeHttpRequests(auth -> auth
        		// 1. 必須要用 / 開頭 去指定路徑
        		// 2.「愈前面的規則優先度愈高」。
        		.requestMatchers("/front-end/ECPay/OrderResultURL", "/front-end/ECPay/ClientBackURL", "/ECPay/OrderResultURL", "/front-end/order_success", "/front-end/order_failure")
        		.hasRole("MEMBER")
                .requestMatchers("/front-end/admin", "/front-end/cart", "/front-end/payment", "/api/front-end/protected/**")
                .hasRole("MEMBER")
                .requestMatchers("/back-end/exhibitor/exhibitor_login", "/front-end/exhibitor_register")
                .permitAll()
//                .requestMatchers("/platform/login", "/platform/register")
//                .permitAll()
                .requestMatchers("/back-end/**", "/api/back-end/**")
                .hasRole("EXHIBITOR")
//                .requestMatchers("/platform/**", "/api/platform/**")
//                .authenticated()
                // .requestMatchers("/api/auth/**") // 不用顯式寫出，底下預設 permit，是給 AuthRestController 用的！
                // .permitAll() 
                .anyRequest()
                .permitAll()
                
                // .permitAll() 完全不攔截
                // .denyAll() 完全拒絕
                // .hasRole("USER")
                // .hasAuthority("SCOPE_read") 
                //（SecurityContextHolder -> SecurityContext 中需要有認證成功的 Authentication）
                	// Session mode -> JSESSEIONID Cookie 對應的 session 還在
                	// JWT mode -. JwtAuthenticationFilter 驗證了 Token -> 建立 Authentication 放進 SecurityContext 
        );

        // ========= 指定使用哪個 AuthenticationProvider（資料庫帳密驗證） =========
        http.authenticationProvider(daoAuthProvider); // 正確掛上前面注入的 Bean（避免 null）

        // ========= 掛上自訂的 JWT Cookie Filter（放在 UsernamePasswordAuthenticationFilter 之前） =========
        http.addFilterBefore(
                jwtFilter,                                  // 你的 JwtCookieAuthenticationFilter
                UsernamePasswordAuthenticationFilter.class  // 位置：比帳密登入 Filter 更前面（先嘗試從 Cookie 建立身份）
        );
        
        // Spring Security - Filter (過濾器層)
        	// 最大單位，所有請求都會經過 Security Filter Chain。
        	// 每個 Filter 負責一種策略：
        		// UsernamePasswordAuthenticationFilter → 處理帳密登入表單
        		// JwtAuthenticationFilter → 解析 JWT Token
        		// BasicAuthenticationFilter → 處理 HTTP Basic 認證
        	// Filter 的任務：
        		// 從請求中取出「憑證」（帳密 / Token / Cookie）
        		// 建立一個 Authentication 物件（通常是「未驗證」狀態）
        		// 丟給 AuthenticationManager 處理
        
        // Filter Chain 是線性的，請求依序通過每個 Filter，而每個 Filter 可選擇
        	// 設定 Security Context （代表「已驗證」）
        	// 不動作（就讓請求繼續往下傳）
        	// 丟 Exception（阻斷流程 -> ExceptionTranlationFilter 接手）

        // ========= 登出：清除 Token Cookie，避免殘留 =========
        // LogoutFilter 加進 Filter Chain
        	// http.logout 本質是配置 LogoutFilter，而內部 .addLogoutHandler 才是選定實作的 LogoutHandler 本人
        		// CookieClearingLogoutHandler
        		// SecurityContextLogoutHandler ( session.invalidate() 等用途，非用在 JWT )
        		// 若為 Storage 版 JWT ：前端掌控，後端 logout handler 只能告知 → 由前端清。
        // 即使你的專案是 SSR 頁面，只要頁面上能跑一點 JS（不是純靜態 HTML），你就可以在「登出」按鈕上綁一個 JS handler：
        
//        http.logout(logout -> logout
//                .logoutUrl("/api/auth/logout/member")                         // 指定登出端點（前端呼叫這個）（預設 /logout 登出，並清掉 session，此處改為 api 風格的 logout）
//                .addLogoutHandler(new CookieClearingLogoutHandler(     // 透過 Handler 清除 Cookie
//                        MEM_ACCESS_COOKIE, MEM_REFRESH_COOKIE                  // 兩顆 Token Cookie 一起清
//                ))
//                // 當用 Cookie 存 JWT 而非 LocalStorage，就可以使用 Spring 現成的 CookieClearingLogoutHandler 去清掉 Cookie
//                .logoutSuccessHandler((req, res, auth) ->              // 登出成功的回應
//                        res.setStatus(204)                             // 回 204 No Content（前端好處理）
//                )
//                // 通常不用設定 Failure Handler 
//                .logoutUrl("/api/auth/logout/exhibitor")
//                .addLogoutHandler(new CookieClearingLogoutHandler(
//                		EXHIB_ACCESS_COOKIE, EXHIB_REFRESH_COOKIE
//                ))
//                .logoutSuccessHandler((req, res, auth) ->
//                		res.setStatus(204)
//                )
//        );
        
        // 以上是錯誤寫法（後者會覆蓋前者 .logoutUrl）
        
        http.logout(logout -> logout
        	    .logoutRequestMatcher(new OrRequestMatcher( // 同時攔截兩個路徑
        	        new AntPathRequestMatcher("/api/auth/logout/member", "POST"),
        	        new AntPathRequestMatcher("/api/auth/logout/exhibitor", "POST")
        	    ))
        	    .addLogoutHandler(customLogoutHandler()) // 自訂 Handler -> 呼叫 customLogoutHandler() → 需要被呼叫才能產生 Bean。
        	    .logoutSuccessHandler((req, res, auth) -> res.setStatus(204)) // 登出成功回 204
        	);


        // 所有頁面可共用「登出模組」
        // async function logoutAndRedirect() {
        	// const res = await fetch("/api/auth/logout", {
        		// method: "POST",
        		// credentials: "include" // 瀏覽器的行為設定 -> 要不要附帶憑證（credentials），而 cookies 為其一
        	// });
        	// if (res.status === 204) { // 可以依需求決定要回什麼，但對 REST API 來說，204 最乾淨、最直觀。-> 204 = 登出成功，無內容 → 前端自己決定跳去哪裡。
        		// window.location.href = "/front-end/login";
        	// }
    	// }
        
        // 同網域請求 → 預設會帶 Cookie，因為屬於「same-origin」。
        // 跨網域請求 → 預設 不會帶 Cookie（安全原因）。
        	// 所以如果你是 前後端分離（不同 domain，例如 frontend.com ↔ api.backend.com），就必須顯式指定：credentials: "include"
        	// Cookie 本身就是存在瀏覽器裡的小資料。
        	// 「要不要帶 Cookie 給某個請求」這件事，由 credentials 控制。	-> omit | same-origin | include 三種值

        return http.build(); // 建構並回傳 SecurityFilterChain
    }
}
