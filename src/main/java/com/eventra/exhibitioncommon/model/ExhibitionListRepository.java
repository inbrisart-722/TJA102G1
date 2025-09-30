package com.eventra.exhibitioncommon.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventra.exhibition.model.ExhibitionVO;

public interface ExhibitionListRepository extends JpaRepository<ExhibitionVO, Integer> {
	
	/* ===== 首頁 (沒有票價、沒有平均評分) ===== */
	
	/* 首頁熱門 TopN */
	@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +
		            "COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png'), " +
		            "SUM(s.exhibition_page_view_count) AS totalViews " +
		            "FROM exhibition_page_popularity_stats s " +
		            "JOIN exhibition e ON s.exhibition_id = e.exhibition_id " +
		            "WHERE s.view_date >= DATE_SUB(CURRENT_DATE, INTERVAL :days DAY) " +
		            "AND e.exhibition_status_id IN (3,4) " +
		            "GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape " +
		            "ORDER BY totalViews DESC " +
		            "LIMIT :topN", nativeQuery = true)
	List<Object[]> findTopNPopularExhibitionsLastNDays(@Param("topN") int topN, @Param("days") int days);
	
	/* 首頁最新 TopN */
	@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +
		            "COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png') " +
		            "FROM exhibition e " +
		            "WHERE e.exhibition_status_id IN (3,4) " +
		            "ORDER BY e.exhibition_id DESC " +
		            "LIMIT :topN", nativeQuery = true)
	List<Object[]> findTopNLatestExhibitions(@Param("topN") int topN);
	
	
	/* ===== 熱門展覽清單頁（分頁, 含票價、平均星數、totalViews） ===== */
	@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +
		            "COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png'), " +
		            "MIN(ett.price) AS minPrice, MAX(ett.price) AS maxPrice, " +
		            "e.start_time, e.end_time, e.location, " +
		            "CASE WHEN e.total_rating_count > 0 " +
		            "THEN ROUND(e.total_rating_score / e.total_rating_count, 1) ELSE 0 END AS averageRatingScore, " +
		            "e.total_rating_count, " +
		            "SUM(s.exhibition_page_view_count) AS totalViews " +
		            "FROM exhibition_page_popularity_stats s " +
		            "JOIN exhibition e ON s.exhibition_id = e.exhibition_id " +
		            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +
		            "WHERE s.view_date >= DATE_SUB(CURRENT_DATE, INTERVAL :days DAY) " +
		            "AND e.exhibition_status_id IN (3,4) " +
		            "GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
		            "e.start_time, e.end_time, e.location, e.total_rating_count, e.total_rating_score " +
		            "ORDER BY totalViews DESC " +
		            "LIMIT :size OFFSET :offset", nativeQuery = true)
	List<Object[]> findPopularExhibitionsLastNDaysPaged(@Param("days") int days,
                                                 @Param("size") int size,
                                                 @Param("offset") int offset);

	@Query(value = "SELECT COUNT(DISTINCT e.exhibition_id) " +
		            "FROM exhibition_page_popularity_stats s " +
		            "JOIN exhibition e ON s.exhibition_id = e.exhibition_id " +
		            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +
		            "WHERE s.view_date >= DATE_SUB(CURRENT_DATE, INTERVAL :days DAY) " +
		            "AND e.exhibition_status_id IN (3,4)", nativeQuery = true)
	int countPopularExhibitionsLastNDays(@Param("days") int days);
    
    
	/* ===== 最新展覽清單頁 (分頁, 含票價、平均星數) ===== */
	@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +
		            "COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png'), " +
		            "MIN(ett.price) AS minPrice, MAX(ett.price) AS maxPrice, " +
		            "e.start_time, e.end_time, e.location, " +
		            "CASE WHEN e.total_rating_count > 0 " +
		            "THEN ROUND(e.total_rating_score / e.total_rating_count, 1) ELSE 0 END AS averageRatingScore, " +
		            "e.total_rating_count " +
		            "FROM exhibition e " +
		            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +
		            "WHERE e.exhibition_status_id IN (3,4) " +
		            "GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
		            "e.start_time, e.end_time, e.location, e.total_rating_count, e.total_rating_score " +
		            "ORDER BY e.exhibition_id DESC " +
		            "LIMIT :size OFFSET :offset", nativeQuery = true)
	List<Object[]> findLatestExhibitionsPaged(@Param("size") int size,
	                                       @Param("offset") int offset);
	
	@Query(value = "SELECT COUNT(DISTINCT e.exhibition_id) " +
		            "FROM exhibition e " +
		            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +
		            "WHERE e.exhibition_status_id IN (3,4)", nativeQuery = true)
	int countLatestExhibitions();
    
    
	/* ===== 展商主頁 (分頁, 含票價、平均星數) ===== */
	@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +
		            "COALESCE(e.photo_landscape, '/img/0_exhibition/ChatGPT_exhibition_1.png'), " +
		            "MIN(ett.price) AS minPrice, MAX(ett.price) AS maxPrice, " +
		            "e.start_time, e.end_time, e.location, " +
		            "CASE WHEN e.total_rating_count > 0 " +
		            "THEN ROUND(e.total_rating_score / e.total_rating_count, 1) ELSE 0 END AS averageRatingScore, " +
		            "e.total_rating_count " +
		            "FROM exhibition e " +
		            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +
		            "WHERE e.exhibitor_id = :exhibitorId " +
		            "AND e.exhibition_status_id IN (3,4) " +
		            "GROUP BY e.exhibition_id, e.exhibition_name, e.photo_landscape, " +
		            "e.start_time, e.end_time, e.location, e.total_rating_count, e.total_rating_score " +
		            "ORDER BY e.start_time DESC " +
		            "LIMIT :size OFFSET :offset", nativeQuery = true)
	List<Object[]> findExhibitionsByExhibitorPaged(@Param("exhibitorId") Integer exhibitorId,
	                                            @Param("size") int size,
	                                            @Param("offset") int offset);
	
	@Query(value = "SELECT COUNT(DISTINCT e.exhibition_id) " +
		            "FROM exhibition e " +
		            "JOIN exhibition_ticket_type ett ON e.exhibition_id = ett.exhibition_id " +
		            "WHERE e.exhibitor_id = :exhibitorId " +
		            "AND e.exhibition_status_id IN (3,4)", nativeQuery = true)
	int countExhibitionsByExhibitor(@Param("exhibitorId") Integer exhibitorId);

}
