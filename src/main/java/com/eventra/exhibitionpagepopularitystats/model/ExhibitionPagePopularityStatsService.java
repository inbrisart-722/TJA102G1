package com.eventra.exhibitionpagepopularitystats.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ExhibitionPagePopularityStatsService {

	@Autowired
	ExhibitionPagePopularityStatsRepository repository;

	@Transactional
	public void recordTodayView(Integer exhId) {
		// 更新結果存入updated變數
		int updated = repository.incrementTodayViewCount(exhId);

		// 如果updated = 0, 則新增一筆紀錄
		if (updated == 0) {
			try {
				ExhibitionPagePopularityStatsVO stats = new ExhibitionPagePopularityStatsVO();
				stats.setExhibitionId(exhId); // 只setExhibitionId, 其餘由 MySQL 新增
				repository.save(stats);

			} catch (DataIntegrityViolationException e) {
				// 同時首次點擊(新增時的唯一性問題)
				// 再次呼叫update, 確保資料正確被累加
				repository.incrementTodayViewCount(exhId);
			}
		}
	}

}
