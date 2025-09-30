package com.eventra.map.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eventra.exhibition.model.ExhibitionVO;

public interface MapExploreRepository extends JpaRepository<ExhibitionVO, Integer> {
    /*
     * 查詢使用者座標半徑範圍內的展覽(地圖探索頁用)
     * Haversine 公式計算兩點距離 (單位: 公里)
     * */
	
	@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +
		            "e.location, e.longitude, e.latitude, " +
		            "COALESCE(e.photo_landscape, '/img/0_exhibition/default.png'), " +
		            "e.start_time, e.end_time, " +
		            "CASE WHEN e.total_rating_count > 0 " +
		            "THEN ROUND(e.total_rating_score / e.total_rating_count, 1) ELSE 0 END AS averageRatingScore, " +
		            "e.total_rating_count " +
		            "FROM exhibition e " +
		            "WHERE e.end_time >= CURDATE() " +
		            "AND e.longitude IS NOT NULL " +
		            "AND e.latitude IS NOT NULL",
		            nativeQuery = true)
	List<Object[]> findNearbyExhibitionsNative();
	
		
		// 日期區間篩選(暫保留)
		@Query(value = "SELECT e.exhibition_id, e.exhibition_name, " +
		               "e.location, e.longitude, e.latitude, " +
		               "COALESCE(e.photo_landscape, '/img/0_exhibition/default.png'), " +
		               "e.start_time, e.end_time, " +
		               "e.total_rating_count " +
		               "FROM exhibition e " +
//		               "WHERE e.exhibition_status_id IN (3,4) " +
		               "WHERE (e.start_time <= :dateTo) " +   // 展覽開始時間要早於篩選結束日
		               "AND (e.end_time >= :dateFrom)",     // 展覽結束時間要晚於篩選開始日
		               nativeQuery = true)
		List<Object[]> findNearbyExhibitionsNative(String dateFrom, String dateTo);
	
	
}
