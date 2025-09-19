package com.eventra.exhibitioncommon.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventra.exhibition.model.ExhibitionVO;

public interface ExhibitionListRepository extends JpaRepository<ExhibitionVO, Integer> {
	// 處理前台頁面所有需要"展覽清單"的固定/常用查詢, 不處理動態搜尋條件
	// 用於 首頁熱門/最新展覽清單、熱門/最新展覽清單頁
	// 動態搜尋由 ExhibitionUtilCompositeQuery + Service 層組裝負責

	/* 
	 * [ Exhibition 時間欄位說明 ]
	 * 
	 * 1. 資料庫 (Table)
	 * 　　　exhibition.start_time, end_time, ticket_start_time : TIMESTAMP
	 * 
	 * 2. Entity (ExhibitionVO)
	 * 　　　對應型別使用 LocalDateTime
	 * 　　　若使用 JPQL / Criteria 查 VO, Hibernate 會自動處理 TIMESTAMP <<=>> LocalDateTime
	 * 
	 * 3. Repository 查詢行為
	 *      根據查詢場景區分:
	 *      A) 複雜查詢 (需要 SUM / GROUP BY / DATE_SUB 等)
	 *           使用 native query, JDBC Driver 回傳 java.sql.Timestamp
	 *           Service 層手動轉換 ((Timestamp) r[i]).toLocalDateTime()
	 *
	 *      B) 簡單查詢 (單純列表，不含聚合函數)
	 *           使用 JPQL Constructor Expression
	 *           Repository 直接回 DTO, 由 Hibernate 自動轉換時間
	 *
	 * 4. DTO (ExhibitionListDTO)
	 *      欄位使用 LocalDateTime
	 *      若來源為 native query, Service 層手動轉換
	 *      若來源為 JPQL Constructor, Hibernate 自動轉換
	 */
	
	
	/* ===== 首頁 ===== */
	
	/* 熱門展覽區, 查詢統計近"n"天的熱門展覽列表, 前"n"筆 */
	@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +											// exhibitionId[0]、exhibitionName[1]
					"COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png'), " +  		// photoLandscape[2]
					"SUM(s.exhibition_page_view_count) AS totalViews " +									// totalViews[3], 排序用
					"FROM exhibition_page_popularity_stats s " +
					"JOIN exhibition e ON s.exhibition_id = e.exhibition_id " + 							// JOIN exhibition
					"WHERE s.view_date >= DATE_SUB(CURRENT_DATE, INTERVAL :days DAY) " +					// 自今日起算往前推n天
					"AND e.exhibition_status_id IN (3, 4) " +  												// 展覽狀態: 3尚未開賣,4售票中
					"GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape " +
					"ORDER BY totalViews DESC " +
					"LIMIT :topN",																			// 抓取n筆
					nativeQuery = true)
	List<Object[]> findTopNPopularExhibitionsLastNDays(@Param("topN") int topN, @Param("days") int days);
	
	/* 最新展覽區, 查詢最新展覽列表, 前"n"筆 */
	@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +											// exhibitionId[0]、exhibitionName[1]
					"COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png') " +			// photoLandscape[2]
					"FROM exhibition e " +
					"JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +				// JOIN exhibition_ticket_type
					"WHERE e.exhibition_status_id IN (3, 4) " +  											// 展覽狀態: 3尚未開賣,4售票中
					"GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
					"e.start_time, e.end_time, e.location, e.total_rating_count " +
					"ORDER BY exhibition_id DESC " +
					"LIMIT :topN",																			// 抓取n筆
					nativeQuery = true)
	List<Object[]> findTopNLatestExhibitions(@Param("topN") int topN);

	
	// ========================================================================================================================
	
	
	/* ===== 熱門展覽清單頁/最新展覽清單頁 ===== */
	
	/* 熱門 - 查詢統計近"n"天的熱門展覽列表 */
    @Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +											// exhibitionId[0]、exhibitionName[1]
		            "COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png'), " +			// photoLandscape[2]
		            "MIN(ett.price) AS minPrice, " +														// minPrice[3]
		            "MAX(ett.price) AS maxPrice, " +														// maxPrice[4]
		            "e.start_time, " +																		// startTime[5]
		            "e.end_time, " +																		// endTime[6]
		            "e.location, " +																		// location[7]
		//            "e.average_rating_score, " +													// averageRatingScore[]
					"e.total_rating_count, " +																// ratingCount[8]
		            "SUM(s.exhibition_page_view_count) AS totalViews " +									// totalViews[9], 排序用
		            "FROM exhibition_page_popularity_stats s " +
		            "JOIN exhibition e ON s.exhibition_id = e.exhibition_id " +								// JOIN exhibition
		            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +				// JOIN exhibition_ticket_type
		            "WHERE s.view_date >= DATE_SUB(CURRENT_DATE, INTERVAL :days DAY) " +					// 自今日起算往前推n天
		            "AND e.exhibition_status_id IN (3, 4) " +  												// 展覽狀態: 3尚未開賣, 4售票中
		            "GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
		            "e.start_time, e.end_time, e.location, e.total_rating_count " + 				// , e.average_rating_score
		            "ORDER BY totalViews DESC",
		            nativeQuery = true)
    List<Object[]> findPopularExhibitionsLastNDays(@Param("days") int days);
    
    /* 最新 - 查詢最新展覽列表 */
    @Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +											// exhibitionId[0]、exhibitionName[1]
		            "COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png'), " +			// photoLandscape[2]
		            "MIN(ett.price) AS minPrice, " + 														// minPrice[3]
		            "MAX(ett.price) AS maxPrice, " +														// maxPrice[4]
		            "e.start_time, "+ 																		// startTime[5]
		            "e.end_time, "+ 																		// endTime[6]
		            "e.location, " +																		// location[7]
		            "e.total_rating_count " +																// ratingCount[8]
		            "FROM exhibition e " +
		            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +				// JOIN exhibition_ticket_type
		            "WHERE e.exhibition_status_id IN (3, 4) " +  											// 展覽狀態: 3尚未開賣, 4售票中
		            "GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
		            "e.start_time, e.end_time, e.location, e.total_rating_count " +
		            "ORDER BY exhibition_id DESC ",
		            nativeQuery = true)
    List<Object[]> findLatestExhibitions();
    
}
