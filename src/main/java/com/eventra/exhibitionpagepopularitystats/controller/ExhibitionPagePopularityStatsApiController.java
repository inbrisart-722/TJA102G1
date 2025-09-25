package com.eventra.exhibitionpagepopularitystats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.exhibitionpagepopularitystats.model.ExhibitionPagePopularityStatsService;

@RestController // 處理API
@RequestMapping("/api/exhibitionPagePopularityStats")
public class ExhibitionPagePopularityStatsApiController {

	@Autowired
	ExhibitionPagePopularityStatsService popularSvc;
	
    // 記錄展覽當日瀏覽數 (每呼叫一次就 +1)
    // @param exhId 展覽 ID
	@PostMapping("/view/{exhId}")
	public String recordView(@PathVariable("exhId") Integer exhId) {
	    popularSvc.recordTodayView(exhId);
	    return "exhibitionId= " + exhId; // 回傳字串給前端
	}
}
