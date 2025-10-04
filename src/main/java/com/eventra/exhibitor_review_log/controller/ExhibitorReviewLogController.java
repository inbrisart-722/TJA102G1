package com.eventra.exhibitor_review_log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.exhibitor_review_log.model.ExhibitorReviewLogService;

@Controller
@RequestMapping("/platform")
public class ExhibitorReviewLogController {

	@Autowired 
	private ExhibitorReviewLogService svc;
	
	@PostMapping("/exhibitor-review")
	public String exhibitorReview(
			@RequestParam(name = "exhibitorId") Integer exhibitorId,
			@RequestParam(name = "rejectReason", required = false) String rejectReason,
			Model model) {
		System.out.println(exhibitorId);
		System.out.println(rejectReason);
		try {
			if(rejectReason == null || rejectReason.isBlank()) {
				// 1-1. 審核通過
				svc.reviewToSuccess(exhibitorId);
			}
			else {
				// 1-2. 審核失敗
				svc.reviewToFailure(exhibitorId, rejectReason);
			}
			// 2-1. svc 沒有錯誤（含信件發送成功）
			return "redirect:/platform/exhibitor";
			// flashAttribute??
		}
		catch (Exception e) {
			// 2-1. svc 有錯誤（含信件發送失敗）
			
			// flashAttribute??
			return "redirect:/platform/exhibitor";
		}
	}
}
