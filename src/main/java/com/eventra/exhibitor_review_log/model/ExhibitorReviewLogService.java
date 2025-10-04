package com.eventra.exhibitor_review_log.model;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.eventra.exhibitor.model.ExhibitorRepository;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.util.JsonCodec;
import com.util.RedisPoolExecutor;
import com.util.SpringMailService;

@Service
@Transactional
public class ExhibitorReviewLogService {
	
	@Value("${spring.mail.test.host}") 
	private String domain;
	
	@Autowired
	private ExhibitorRepository exhibitorRepo;
	@Autowired 
	private ExhibitorReviewLogRepository logRepo;
	@Autowired
	private ExhibitorReviewLogRedisRepository exhibitorRedisRepo;
	@Autowired
	private SpringMailService MAIL_SERVICE;
	@Autowired
	private TemplateEngine TEMPLATE_ENGINE;
	
//	private Integer exhibitorReviewId;
//	private Integer exhibitorId;
//	private String rejectReason;
//	private Timestamp reviewedAt;
	
	public void reviewToSuccess(Integer exhibitorId) throws Exception{
		// 1. 存 DB
		ExhibitorReviewLogVO vo = new ExhibitorReviewLogVO();
		vo.setExhibitorId(exhibitorId);
		logRepo.save(vo);
		
		// 展商本人狀態也要存
		ExhibitorVO exhibitor = exhibitorRepo.findById(exhibitorId).orElseThrow();
		exhibitor.setReviewStatusId(2); // 2 = 已核准
		exhibitorRepo.save(exhibitor);
		
		// 2. 寄信
		Optional<ExhibitorVO> voOP = exhibitorRepo.findById(exhibitorId);
		String email = null;
		if(voOP.isPresent()) email = voOP.get().getEmail();
		
		// 單一參數
		Context context = new Context();
		context.setVariable("domain", domain);
		String html = TEMPLATE_ENGINE.process("platform/exhibitor_review_success", context);

		MAIL_SERVICE.sendHtmlMail(email, "【Eventra】恭喜通過展商申請 📩", html);
		
		// 3. [若錯誤，丟 RuntimeException !] -> 給 controller 處理
	}
	
	public void reviewToFailure(Integer exhibitorId, String rejectReason) throws Exception{
		// 1. 存 DB
		ExhibitorReviewLogVO vo = new ExhibitorReviewLogVO();
		vo.setExhibitorId(exhibitorId);
		vo.setRejectReason(rejectReason);
		logRepo.save(vo);
		
		// 展商本人狀態也要存
		ExhibitorVO exhibitor = exhibitorRepo.findById(exhibitorId).orElseThrow();
		exhibitor.setReviewStatusId(3); // 3 = 未通過
		
		exhibitorRepo.save(exhibitor);
		
		// 2. 寄信前存 redis
		String token = exhibitorRedisRepo.saveFailureToken(exhibitorId);
		
		// 3. 寄信
		Optional<ExhibitorVO> voOP = exhibitorRepo.findById(exhibitorId);
		String email = null;
		if(voOP.isPresent()) email = voOP.get().getEmail();
		
		// 共有 3 個參數
		Context context = new Context();
		context.setVariable("domain", domain);
		context.setVariable("rejectReason", rejectReason);
		context.setVariable("token", token);
		String html = TEMPLATE_ENGINE.process("platform/exhibitor_review_failure", context);
		
		MAIL_SERVICE.sendHtmlMail(email, "【Eventra】展商申請失敗 📩", html);
		
		// 4. [若錯誤，丟 RuntimeException !] -> 給 controller 處理
	}
	
}
