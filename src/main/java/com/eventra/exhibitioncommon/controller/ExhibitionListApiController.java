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

	/* ===== 搜尋展覽清單頁 ===== */
	@PostMapping("/search")
	public Map<String, Object> searchExhibitionsPaged(
            @RequestBody Map<String, Object> criteria,
            @RequestParam(defaultValue = "1") int page) {

        if (criteria == null || criteria.isEmpty()) {
            return Collections.emptyMap();
        }

        // 將 Object 轉成 Map<String, String[]>
        Map<String, String[]> converted = new java.util.HashMap<>();
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
                converted.put(key, new String[]{String.valueOf(value)});
            }
        }

        return service.searchExhibitionsPaged(converted, page, PAGE_SIZE);
    }
	
//	public List<ExhibitionListDTO> searchExhibitions(@RequestBody Map<String, Object> criteria) {
////		System.out.println("criteria raw: " + criteria);
//
//		// 將 Object 轉成 Map<String, String[]>, 方便給 ExhibitionUtilCompositeQuery 使用
//		Map<String, String[]> converted = new HashMap<>();
//
//		for (Map.Entry<String, Object> entry : criteria.entrySet()) {
//			String key = entry.getKey();
//			Object value = entry.getValue();
//
//			if (value instanceof List) {
//				List<?> list = (List<?>) value;
//				String[] arr = new String[list.size()];
//				for (int i = 0; i < list.size(); i++) {
//					arr[i] = String.valueOf(list.get(i));
//				}
//				converted.put(key, arr);
//			} else if (value != null) {
//				converted.put(key, new String[] { String.valueOf(value) });
//			}
//		}
//
////		System.out.println("criteria converted: " + converted);
//
//		// 直接走 ExhibitionUtilCompositeQuery (透過 Service)
//		List<ExhibitionListDTO> result = service.searchExhibitions(converted);
//		return result != null ? result : Collections.emptyList();
//	}

	// 測試用
//	@PostMapping("/search/simple")
//	public List<ExhibitionListDTO> searchExhibitionsSimple(@RequestBody Map<String, Object> criteria) {
//		String keyword = criteria.get("keyword") != null ? "%" + criteria.get("keyword").toString() + "%" : null;
//
//		String startDate = criteria.get("date_from") != null ? criteria.get("date_from").toString() + " 00:00:00"
//				: "1970-01-01 00:00:00";
//
//		String endDate = criteria.get("date_to") != null ? criteria.get("date_to").toString() + " 23:59:59"
//				: "2999-12-31 23:59:59";
//
//		// 支援多地區
//		List<String> regions = new ArrayList<>();
//		Object regionObj = criteria.get("regions");
//		if (regionObj instanceof List) {
//			for (Object o : (List<?>) regionObj) {
//				regions.add("%" + o.toString() + "%"); // LIKE 用
//			}
//		} else if (regionObj instanceof String) {
//			for (String s : regionObj.toString().split(",")) {
//				regions.add("%" + s.trim() + "%");
//			}
//		}
//
//		return service.searchExhibitionsByNameAndDateRange(keyword, startDate, endDate, regions);
//	}

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
