package com.eventra.exhibitionpagepopularitystats.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ExhibitionPagePopularityStatsRepository extends JpaRepository<ExhibitionPagePopularityStatsVO, Integer> {
	
	// 更新當日展覽點擊數
	// 回傳結果: 0(當天無資料) 或 1(表示表示展覽有紀錄且累加成功)
	@Modifying
	@Query("UPDATE ExhibitionPagePopularityStatsVO e " +
	       "SET e.exhibitionPageViewCount = e.exhibitionPageViewCount + 1 " +
	       "WHERE e.exhibitionId = :exhId AND e.viewDate = CURRENT_DATE")
	int incrementTodayViewCount(@Param("exhId") Integer exhibitionId);


}
