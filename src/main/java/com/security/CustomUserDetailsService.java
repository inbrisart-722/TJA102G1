//package com.security;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import com.eventra.exhibitor.model.ExhibitorRepository;
//import com.eventra.exhibitor.model.ExhibitorVO;
//import com.eventra.member.test.model.TestMemberRepository;
//import com.eventra.member.test.model.TestMemberVO;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final TestMemberRepository MEMBER_REPO;
//    private final ExhibitorRepository EXHIBITOR_REPO;
//
//    public CustomUserDetailsService(TestMemberRepository memberRepo, ExhibitorRepository exhibitorRepo) {
//        this.MEMBER_REPO = memberRepo;
//        this.EXHIBITOR_REPO = exhibitorRepo;
//    }
//
//    /**
//     * Spring Security 在登入時會呼叫這個方法。
//     * 根據自己的專案情境，我們用 email 當作「username」來查會員。
//     */
//    @Override
//    // 這個 username 不一定真的是「帳號名稱」，只是個 唯一識別字串。可以用 email、手機號、memberId ... ...
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        TestMemberVO member = MEMBER_REPO.findByEmail(username).orElse(null);
//        ExhibitorVO exhibitor = EXHIBITOR_REPO.findByBusinessIdNumber(username).orElse(null);
//        
//        String loginIdentity = null;
//        if(member != null || exhibitor != null)
//        	loginIdentity = member != null ? "MEMBER" : "EXHIBITOR";
//        
//        switch (loginIdentity) {
//        	case "MEMBER":
//        		return org.springframework.security.core.userdetails.User
//        				.withUsername(member.getEmail())
//        				.password(member.getPasswordHash())
//        				.roles("MEMBER")
//        				.build();
//        		
//        	case "EXHIBITOR":
//        		return org.springframework.security.core.userdetails.User
//        				.withUsername(exhibitor.getBusinessIdNumber())
//        				.password(exhibitor.getPasswordHash())
//        				.roles("EXHIBITOR")
//        				.build();
//        		
//        	default: throw new UsernameNotFoundException("User not found");
//        }
//        
//        // 必須丟 UsernameNotFoundException，因為這是 Spring Security API 規範。
//        	// 1. 中斷流程，避免 null pointer。
//        	// 2. DaoAuthenticationProvider 會捕捉 UsernameNotFoundException。
//        	// 3. 它會統一轉換成 BadCredentialsException。
//        	// 4. ExceptionTranslationFilter 再把它丟給 AuthenticationEntryPoint → 前端只會收到「Bad credentials」或 401。
//        	// 5. 👉 所以這裡訊息主要給 開發者 / 運維人員 看，不會暴露給攻擊者。
//        
//        // 如果回 null 或自己回「帳號不存在」，攻擊者可以爆破 email/username 來猜哪些帳號存在。
//        // Spring Security 統一處理後 → 外部只會看到「Bad credentials」，模糊掉「帳號不存在 vs 密碼錯誤」。
//        // 丟 UsernameNotFoundException 可以乾淨地中斷流程，交給 ExceptionTranslationFilter → AuthenticationEntryPoint 處理。
//
//        // 1. 用 Spring 內建的 User（UserDetails 的一個實作類） 來包裝
//        // 2. static Builder pattern (User.UserBuilder)
//        // 會回傳一個完整的 UserDetails 物件，回傳給 Spring Security 用來做登入驗證
//        
////        return org.springframework.security.core.userdetails.User
////                .withUsername(member.getEmail())       // 指定這個 user 的「唯一識別字串」-> 用 email 當 username
////                .password(member.getPasswordHash())    // 指定這個 user 的「雜湊後密碼」-> 注意：要事先用 BCrypt 存進 DB
////                											// 必須是註冊時用 BCryptPasswordEncoder.encode() 存進 DB 的雜湊值
////                											// Spring Security 之後會用 PasswordEncoder.matchers(raw, hash) 來比對
////                
////                .authorities("ROLE_USER")              // 指定這個 user 的「權限／角色」-> Spring Security 會用這個來判斷「使用者能不能存取某個資源」
////                .roles("USER")
////                
////                .accountLocked(false)                  // 目前設計 MemberVO 沒有鎖定欄位 → 固定 false -> 如果 true 表示帳號鎖定 登入會被拒絕
////                .disabled(false)                       // 目前設計 MemberVO 沒有停用欄位 → 固定 false -> 如果 true 表示帳號停用 登入會被拒絕
////                .build();
//        
//        // ***** 重要 *****
//        // 「使用者的角色 / 權限」在 Spring Security 裡，就是從 UserDetailsService 這條線餵進來的。
//        	// UserDetails 介面裡有一個方法：Collection<? extends GrantedAuthority> getAuthorities(); // 這就是用來裝「角色 / 權限」的。
//        // GrantedAuthority 是核心
//        	// Spring Security 的授權判斷 最終都是看 authorities
//        	// 也就是 Authentication.getAuthorities() 回傳的集合
//        	// hasRole() 與 hasAuthority() 其實只是對 同一個集合 的不同用法
//        // .roles(...) 與 .authorities(...) 的差別
//        	// .roles("USER")
//        		// 這個方法其實會自動幫你加前綴 ROLE_
//        		// 所以最終放進去的 authorities = ["ROLE_USER"]
//        	// .authorities("READ")
//        		// 不會加前綴，我們自己決定字串長怎樣
//        // .hasRole() 與 .hasAuthority()
//        	// .hasRole("USER")
//        		// Spring Security 內部會自動補上 ROLE_ 前綴去比對。
//        		// 等於檢查 Authentication.getAuthorities() 裡有沒有 "ROLE_USER"。
//        	// .hasAuthority("READ")
//        		// 不會加前綴，直接比對 "READ"。
//        // 常見用法差異
//        	// 角色 (Role) → 粗粒度，用來描述使用者身分
//        	// 權限 (Authority) → 細粒度，用來描述操作能力
//    }
//}
