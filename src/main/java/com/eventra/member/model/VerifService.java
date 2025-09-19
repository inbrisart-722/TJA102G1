package com.eventra.member.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.util.SpringMailService;

import jakarta.mail.MessagingException;

@Service
@Transactional
public class VerifService {
	
	private final String DOMAIN; 
			
	private final MemberRepository MEMBER_REPO;
	private final MemberRedisRepository MEMBER_REDIS_REPO;
	private final SpringMailService MAIL_SERVICE;
	private final TemplateEngine TEMPLATE_ENGINE;
	// 模板引擎的核心物件（預設是 SpringTemplateEngine, 底層為 Thymeleaf）
		// 載入 HTML 模板檔案 -> 把變數替換入 ${} -> 輸出成一個純 HTML 字串
	// Spring web 會透過 Spring MVC 自動呼叫 TemplateEngine
	// 寄信、非 Web 場景，則需要自己 @Autowired TemplateEngine，且手動把模板渲染成 HTML 字串。
	
	public VerifService(@Value("${spring.mail.test.host}") String domain, MemberRepository memberRepository, MemberRedisRepository memberRedisRepository, SpringMailService mailService, TemplateEngine templateEngine) {
		this.DOMAIN = domain;
		this.MEMBER_REPO = memberRepository;
		this.MEMBER_REDIS_REPO = memberRedisRepository;
		this.MAIL_SERVICE = mailService;
		this.TEMPLATE_ENGINE = templateEngine;
	}
	
	public CheckIfSendableResDTO checkIfSendable(String email) {
		Long ttl = MEMBER_REDIS_REPO.checkIfSendable(email); // 本體是 long
		
		CheckIfSendableResDTO res = new CheckIfSendableResDTO();
		// 驗證碼發送 已經不需要冷卻
		if(ttl == null) return res.setAllowed(true);
		// 驗證碼發送 還需要等待
		else return res.setAllowed(false).setRemaining(ttl);
	}
	
	public String sendVerifRegistration(SendVerifCodeReqDTO req) throws MessagingException{
		String email = req.getEmail();
		String authType = req.getAuthType();
		
		// 1. 組裝 redis token 所需資訊
		Map<String, String> map = new HashMap<>();
		map.put("email", email);
		map.put("authType", authType);
		
		// uuid 直接呼叫 toString
		String token = MEMBER_REDIS_REPO.createAuthToken(map).toString();
		
		// 2. 送信(簡單版）
		// sendSimpleMail(String to, String subject, String text)
//		MAIL_SERVICE.sendSimpleMail(req.getEmail(), "哈囉", "小測試！！" + uuid);
		
		// 3. 送信（html版）
		
		// (1) org.thymeleaf.context.Context -> 用來裝變數，就像 Map<String, Object>
		Context context = new Context();
		context.setVariable("token", token);
		context.setVariable("domain", DOMAIN);
		// (2) 模板引擎 -> 找檔案 -> 把 context 變數替換掉模板中的 ${} -> 存入 html 變數
		String html = TEMPLATE_ENGINE.process("front-end/verif_registration_mail", context); 
		
		// (3) sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException
		MAIL_SERVICE.sendHtmlMail(email, "【Eventra】請驗證您的 Email 📩", html);
		return "SUCCESS";
	}
	
	public VerificationResult verifRegistration(String token) {
		
		String authType = MEMBER_REDIS_REPO.verifyToken(token);
		
		if(authType == null) return VerificationResult.TOKEN_NOT_FOUND;
		if(!"REGISTRATION".equals(authType)) return VerificationResult.TOKEN_TYPE_INVALID;
		return VerificationResult.SUCCESS;
	}
	
	public String findEmailByToken(String token) {
		return MEMBER_REDIS_REPO.findEmailByToken(token);
	}
	// sendVerifRegistration -> forgot pass -> change mail
	// verifRegistration -> forgot pass -> change mail
}
