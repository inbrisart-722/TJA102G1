package com.eventra.exhibition_page_popularity_stats.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.order_notification.model.OrderNotificationVO;

public interface ExhibitionPagePopularityStatsRepository extends JpaRepository<ExhibitionPagePopularityStatsVO, Integer> {
	
	// (測試用)查詢某展覽在[指定日期]是否已有紀錄
	@Query(value = "SELECT * FROM exhibition_page_popularity_stats " +
				   "WHERE exhibition_id = :exhId " +
				   "AND view_date = :viewDate",
			nativeQuery = true)
	ExhibitionPagePopularityStatsVO findRecordByDate(@Param("exhId") Integer exhibitionId, @Param("viewDate") java.sql.Date viewDate);

	// 查詢某展覽在[當天]是否已有紀錄
	@Query(value = "SELECT * FROM exhibition_page_popularity_stats " +
				   "WHERE exhibition_id = :exhId " +
				   "AND view_date = CURRENT_DATE",
				   nativeQuery = true)
	ExhibitionPagePopularityStatsVO findTodayRecord(@Param("exhId") Integer exhibitionId);
	
	// 更新當天展覽點擊數+1
	@Transactional
    @Modifying
    @Query(value = "UPDATE exhibition_page_popularity_stats " +
                   "SET exhibition_page_view_count = exhibition_page_view_count + 1 " +
                   "WHERE exhibition_id = :exhId " +
                   "AND view_date = CURRENT_DATE",
                   nativeQuery = true)
    int incrementTodayViewCount(@Param("exhId") Integer exhibitionId);
	
	// 查詢近7 天 點擊率前5 名的展覽 (合併同展覽的點擊數)
    @Query(value = "SELECT exhibition_id " +
            	   "FROM exhibition_page_popularity_stats " +
            	   "WHERE view_date >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY) " +
            	   "GROUP BY exhibition_id " +
            	   "ORDER BY SUM(exhibition_page_view_count) DESC " +
            	   "LIMIT 5",
            	   nativeQuery = true)
    List<Integer> findTop5ExhibitionsLast7Days();

}
