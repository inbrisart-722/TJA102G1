package com.eventra.exhibitioncommon.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSetCommands.SPopCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.exhibition.model.ExhibitionServiceImpl;
import com.eventra.exhibition.model.ExhibitionSidebarResultDTO;
import com.eventra.exhibitioncommon.dto.ExhibitionListDTO;
import com.eventra.exhibitioncommon.model.ExhibitionListService;

@RestController // 處理API
@RequestMapping("/api/exhibitions")
public class ExhibitionListApiController {

	@Autowired
	private ExhibitionListService service;
	@Autowired
	private ExhibitionServiceImpl exhibitionService;
	
	static final int TOPN = 5;
	static final int DAYS = 14;
	static final int PAGE_SIZE = 5;

	@GetMapping("/sidebar")
	public ResponseEntity<ExhibitionSidebarResultDTO> getSidebar(
			@RequestParam(value="exhibitionId", required=false) Integer exhibitionId,
			@RequestParam(value="averageRatingScore", required=false) Double averageRatingScore){
		// 雖然是側欄，但拉評價高到低的
		if(exhibitionId == null) exhibitionId = 0;
		if(averageRatingScore == null) averageRatingScore = 5.1;
		
		return ResponseEntity.ok(exhibitionService.findSidebarExhibitionsByRatingScore(exhibitionId, averageRatingScore));
	}
	
	/* ===== 首頁 ===== */
	@GetMapping("/popular/topN")
	public List<ExhibitionListDTO> getTopNPopular() {
		return service.getTopNPopularExhibitions(TOPN, DAYS);
	}

	@GetMapping("/latest/topN")
	public List<ExhibitionListDTO> getTopNLatest() {
		return service.getTopNLatestExhibitions(TOPN);
	}

	/* ===== 熱門展覽清單頁 ===== */
	@GetMapping("/popular")
    public Map<String, Object> getPopularPaged(
            @RequestParam(defaultValue = "1") int page) {
        return service.getPopularExhibitionsPaged(DAYS, page, PAGE_SIZE);
    }
//	public List<ExhibitionListDTO> getPopular() {
//		return service.getPopularExhibitions(DAYS);
//	}

	/* ===== 最新展覽清單頁 ===== */
	@GetMapping("/latest")
    public Map<String, Object> getLatestPaged(
            @RequestParam(defaultValue = "1") int page) {
        return service.getLatestExhibitionsPaged(page, PAGE_SIZE);
    }
//	public List<ExhibitionListDTO> getLatest() {
//		return service.getLatestExhibitions();
//	}

	/* ===== 搜尋展覽清單頁 (分頁版) ===== */
	@PostMapping("/search")
	public Map<String, Object> searchExhibitionsPaged(
	        @RequestBody Map<String, Object> criteria,
	        @RequestParam(defaultValue = "1") int page) {

		if (criteria == null || criteria.isEmpty()) {
	        // 原本是 return Collections.emptyMap();
	        // 改成回傳正確的分頁物件
	        Map<String, Object> emptyResult = new HashMap<>();
	        emptyResult.put("content", Collections.emptyList());
	        emptyResult.put("page", page);
	        emptyResult.put("size", PAGE_SIZE);
	        emptyResult.put("totalElements", 0L);
	        emptyResult.put("totalPages", 0);
	        return emptyResult;
	    }

	    // 將 Object 轉成 Map<String, String[]>, 方便給 ExhibitionUtilCompositeQuery 使用
	    Map<String, String[]> converted = new HashMap<>();

	    for (Map.Entry<String, Object> entry : criteria.entrySet()) {
	        String key = entry.getKey();
	        Object value = entry.getValue();

	        if (value instanceof List) {
	            List<?> list = (List<?>) value;
	            String[] arr = new String[list.size()];
	            for (int i = 0; i < list.size(); i++) {
	                arr[i] = String.valueOf(list.get(i));
	            }
	            converted.put(key, arr);
	        } else if (value != null) {
	            String val = String.valueOf(value);
	            // 特別處理 regions：如果是逗號分隔字串，就拆成陣列
	            if ("regions".equals(key) && val.contains(",")) {
	                converted.put(key, val.split(","));
	            } else {
	                converted.put(key, new String[]{val});
	            }
	        }
	    }

	    // 呼叫 Service，做 CriteriaQuery + 分頁
	    return service.searchExhibitionsPaged(converted, page, PAGE_SIZE);
	}

	
	/* ===== 展商主頁 ===== */
	// 取得某展商的所有展覽清單
	@GetMapping("/by-exhibitor")
    public Map<String, Object> getExhibitionsByExhibitorPaged(
            @RequestParam("exhibitorId") Integer exhibitorId,
            @RequestParam(defaultValue = "1") int page) {
        if (exhibitorId == null) return Collections.emptyMap();
        return service.getExhibitionsByExhibitorPaged(exhibitorId, page, PAGE_SIZE);
    }
//    public List<ExhibitionListDTO> getExhibitionsByExhibitor(@RequestParam("exhibitorId") Integer exhibitorId) {
//        if (exhibitorId == null) return Collections.emptyList();
//        return service.getExhibitionsByExhibitor(exhibitorId);
//    }
	
}
