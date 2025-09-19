package com.eventra.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.search.model.SearchService;
import com.eventra.search.model.SearchVO;

@RestController // 處理API
@RequestMapping("/api/search")
public class SearchApiController {
	
	/* 
	 * 定位：搜尋紀錄模組 (Search History Module)
	 * 只處理「使用者曾經搜尋過什麼」的資訊，不負責查展覽
	 * 
	 * */

	@Autowired
	private SearchService searchService;

	// 新增單筆搜尋紀錄
	@PostMapping("/add")
	public ResponseEntity<SearchVO> addSearch(@RequestBody SearchVO search) {
	    SearchVO saved = searchService.addSearch(search);
	    if (saved != null) {
	        return ResponseEntity.ok(saved);
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	// 批次新增搜尋紀錄, 登入瞬間 LocalStorage 存到 DB 用 (待處理)
	@PostMapping("/addBatch")
	public List<SearchVO> addSearchBatch(@RequestBody List<SearchVO> searches) {
		return searchService.addSearchBatch(searches);
	}

	// 查詢該會員最近 10 筆搜尋紀錄, 最近搜尋過區塊顯示用
	@GetMapping("/recent/member/{memberId}")
	public List<SearchVO> getRecentSearchesByMember(@PathVariable Integer memberId) {
		return searchService.getRecentSearchesByMember(memberId);
	}

}
