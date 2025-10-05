package com.eventra.search.controller;

import java.sql.Date;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eventra.search.model.SearchService;
import com.eventra.search.model.SearchVO;

@RestController // 處理API
@RequestMapping("/api/search")
public class SearchApiController {

	@Autowired
	private SearchService searchService;

	// 新增單筆搜尋紀錄
	@PostMapping("/add")
	public ResponseEntity<SearchVO> addSearch(@RequestBody SearchVO search) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			try {
				Integer memId = Integer.valueOf(auth.getName());
				search.setMemberId(memId);
			} catch (NumberFormatException e) {
				System.out.println("不是數字: " + auth.getName());
				search.setMemberId(null);
			}
		} else {
			search.setMemberId(null);
		}

		SearchVO saved = searchService.addSearch(search);
		return (saved != null) ? ResponseEntity.ok(saved)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	// 批次新增搜尋紀錄, 登入瞬間 LocalStorage 存到 DB 用
	@PostMapping("/addBatch")
	public ResponseEntity<List<SearchVO>> addSearchBatch(@RequestBody List<SearchVO> searches) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Integer memberId = null;

		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			try {
				memberId = Integer.valueOf(auth.getName());
			} catch (NumberFormatException e) {
				System.out.println("不是數字: " + auth.getName());
			}
		}

		if (memberId != null) {
			for (SearchVO s : searches) {
				s.setMemberId(memberId);
			}
		}

		List<SearchVO> result = searchService.addSearchBatch(searches);
		return ResponseEntity.ok(result);
	}

	// 查詢該會員最近 10 筆搜尋紀錄, 最近搜尋過區塊顯示用
	@GetMapping("/recent/member/{memberId}")
	public List<SearchVO> getRecentSearchesByMember(@PathVariable Integer memberId) {
		return searchService.getRecentSearchesByMember(memberId);
	}

}
