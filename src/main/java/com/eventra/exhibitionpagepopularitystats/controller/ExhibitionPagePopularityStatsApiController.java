package com.eventra.exhibitionpagepopularitystats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.exhibitionpagepopularitystats.model.ExhibitionPagePopularityStatsService;

@RestController
@RequestMapping("/api/exhibitionPagePopularityStats")

public class ExhibitionPagePopularityStatsApiController {

	/**
	 * 當展覽頁載入時, 前端會呼叫 API 記錄一次瀏覽
	 * 
	 */
	
	@Autowired
	ExhibitionPagePopularityStatsService popularSvc;

	@PostMapping("/count/{exhibitionId}")
	public String recordView(@PathVariable("exhibitionId") Integer exhibitionId) {
	    popularSvc.recordTodayView(exhibitionId);
	    return "OK: exhibitionId= " + exhibitionId;
	}
}
