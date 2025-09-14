package com.eventra.exhibitioncommon.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventra.exhibition.model.ExhibitionVO;

public interface ExhibitionListRepository extends JpaRepository<ExhibitionVO, Integer> {
	/* ===== 首頁 ===== */
	
	/* 熱門展覽區, 查詢統計近"n"天的熱門展覽列表, 前"n"筆 */
	@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +											// exhibitionId[0]、exhibitionName[1]
					"COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png'), " +  		// photoLandscape[2]
					"SUM(s.exhibition_page_view_count) AS totalViews " +									// totalViews[3], 排序用
					"FROM exhibition_page_popularity_stats s " +
					"JOIN exhibition e ON s.exhibition_id = e.exhibition_id " + 							// JOIN exhibition
					"WHERE s.view_date >= DATE_SUB(CURRENT_DATE, INTERVAL :days DAY) " +					// 自今日起算往前推n天
					"AND e.exhibition_status_id IN (3, 4) " +  												// 展覽狀態: 尚未開賣,售票中
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
					"WHERE e.exhibition_status_id IN (3, 4) " +  											// 展覽狀態: 尚未開賣,售票中
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
		            "MIN(ett.price) AS minPrice, "+ 														// minPrice[3]
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
