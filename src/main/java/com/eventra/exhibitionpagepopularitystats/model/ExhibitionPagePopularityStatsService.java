package com.eventra.exhibitionpagepopularitystats.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExhibitionPagePopularityStatsService {

	/**
	 * 因為大部分情況都是累加, 所以是走 更新再新增 的作法
	 * 
	 * recordTodayView(exhId)
	 *  - 嘗試更新當日瀏覽數 +1
	 *  - 如果當日還沒有資料 : 新增一筆
	 *  - 如果新增過程遇到衝突(多個使用者同時第一次瀏覽) : 捕捉例外並改成更新
	 *  
	 */
	
	@Autowired
	ExhibitionPagePopularityStatsRepository repository;

	@Transactional
	public void recordTodayView(Integer exhId) {
		int updated = repository.incrementTodayViewCount(exhId);
		
		// System.out.println("[recordTodayView] exhId=" + exhId + ", updated=" + updated);

		if (updated == 0) {
			try {
				ExhibitionPagePopularityStatsVO stats = new ExhibitionPagePopularityStatsVO();
				stats.setExhibitionId(exhId);
				repository.save(stats);
				
				// System.out.println("[recordTodayView] 新增成功 exhId=" + exhId);

			} catch (DataIntegrityViolationException e) {
				// 同時首次點擊(新增時的唯一性問題), 再次呼叫update, 確保資料正確被累加
				
				// System.out.println("[recordTodayView] 捕捉唯一鍵衝突，再次更新 exhId=" + exhId);
				
				repository.incrementTodayViewCount(exhId);
			}
		}
	}

}
