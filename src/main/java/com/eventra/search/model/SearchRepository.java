package com.eventra.search.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<SearchVO, Integer> {

	/*
	 * 非會員搜尋(member_id=null)
	 * 1. 存 LocalStorage + DB
	 * 2. 用 LocalStorage 顯示前端的 最近搜尋過區塊	 * 
	 * 當會員登入/註冊成功時, 馬上執行	 * 1. 當前 LocalStorage 存到 DB (這邊memberId = 該會員)	 * 
	 * 會員搜尋(member_id=該會員)	 * 1. 存 DB	 * 2. LocalStorage 保留即時顯示, 用 DB 顯示前端的 最近搜尋過區塊
	 * 
	 * */
	
	// 查詢該會員最近 10 筆搜尋紀錄, 最近搜尋過區塊顯示用
    List<SearchVO> findTop10ByMemberIdOrderBySearchedAtDesc(Integer memberId);

    // 查詢非會員們(member_id=NULL) 最近 10 筆搜尋紀錄, 最近搜尋過區塊顯示用
    List<SearchVO> findTop10ByMemberIdIsNullOrderBySearchedAtDesc();
}
