package com.eventra.search.model;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SearchRepository extends JpaRepository<SearchVO, Integer> {

	/*
	 * 搜尋紀錄同步機制說明
	 * ==============================================
	 * 1: 非會員搜尋（member_id = NULL）
	 *  - 搜尋紀錄存 LocalStorage + DB (member_id = NULL)
	 *  - 前端「您最近搜尋過」區塊, 資料來源是 LocalStorage
	 *
	 * 2: 會員 登入/註冊成功 "當下"
	 *  - 將 LocalStorage 匿名搜尋紀錄加上 member_id 存到 DB (由 g1_6_search_history.js 的 syncSearchHistoryToDB(memberId) 處理)
	 *  - ※ 這邊不清除 LocalStorage (保留快取)
	 *  - 建立 旗標 (searchSyncDone): 存 sessionStorage, 代表本次登入已完成同步, 防止2次儲存
	 *  - 建立 簽章 (searchSyncSig:<memberId>): 存於 localStorage, 儲存 LocalStorage 的 hash 值 (數位指紋), 下次登入時若內容未變就不同步
	 *
	 * 3: 會員登入後搜尋
	 *  - 搜尋紀錄存 DB (有 member_id)
	 *  - ※ 因為 LocalStorage 仍保留快取, 為了方便前端即時顯示
	 *  - 前端「您最近搜尋過」區塊, 資料來源是 DB
	 *  
	 * 4: 會員登出
	 *  - ※ 這邊不清除 LocalStorage (因為登出而消失, 會很奇怪)
	 *  - 移除旗標 + 簽章
	 *  - 前端「您最近搜尋過」區塊，資料來源是 LocalStorage
	 *  
	 */
	
	// 查詢該會員最近 10 筆搜尋紀錄, 最近搜尋過區塊顯示用
    List<SearchVO> findTop10ByMemberIdOrderBySearchedAtDesc(Integer memberId);

    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
        FROM search
        WHERE ((:memberId IS NULL AND member_id IS NULL) OR member_id = :memberId)
          AND IFNULL(keyword, '') = IFNULL(:keyword, '')
          AND IFNULL(regions, '') = IFNULL(:regions, '')
          AND IFNULL(date_from, '1000-01-01') = IFNULL(:dateFrom, '1000-01-01')
          AND IFNULL(date_to,   '1000-01-01') = IFNULL(:dateTo,   '1000-01-01')
          AND DATE(searched_at) = CURDATE()
        """, nativeQuery = true)
    Integer existsSameTodayRaw(
        @Param("memberId") Integer memberId,
        @Param("keyword") String keyword,
        @Param("regions") String regions,
        @Param("dateFrom") Date dateFrom,
        @Param("dateTo") Date dateTo
    );

    default boolean existsSameToday(
        Integer memberId, String keyword, String regions, Date dateFrom, Date dateTo) {
        Integer result = existsSameTodayRaw(memberId, keyword, regions, dateFrom, dateTo);
        return result != null && result == 1;
    }

}
