package com.eventra.search.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class SearchService {

	@Autowired
	private SearchRepository repository; // 基本CRUD 用

	// 新增單筆搜尋紀錄
	public SearchVO addSearch(SearchVO search) {
		if (search.getRegions() == null) {
			search.setRegions("");
		}
		System.out.println("📩 收到 keyword=" + search.getKeyword() + ", regions=" + search.getRegions());
		return repository.save(search);
	}

	// 批次新增搜尋紀錄, 登入瞬間 LocalStorage 存到 DB 用
	// 開啟交易, 成功commit(), 失敗rollback(), 前端可再重送請求
	@Transactional
	public List<SearchVO> addSearchBatch(List<SearchVO> searches) {
		return repository.saveAll(searches);
	}

	// 查詢該會員最近 10 筆搜尋紀錄, 最近搜尋過區塊顯示用
	public List<SearchVO> getRecentSearchesByMember(Integer memberId) {
		return repository.findTop10ByMemberIdOrderBySearchedAtDesc(memberId);
	}

	// 查詢非會員(member_id=NULL) 最近 10 筆搜尋紀錄, 最近搜尋過區塊顯示用
	public List<SearchVO> getRecentAnonymousSearches() {
		return repository.findTop10ByMemberIdIsNullOrderBySearchedAtDesc();
	}

}
