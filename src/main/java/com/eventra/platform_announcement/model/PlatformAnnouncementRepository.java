package com.eventra.platform_announcement.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface PlatformAnnouncementRepository extends JpaRepository<PlatformAnnouncementVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from platform_announcement where announcement_id = :annId", 
	nativeQuery = true)
	void deleteByAnnId(@Param("annId")int announcementId);
	
	// 模糊查詢
	@Query(value = "SELECT * FROM platform_announcement "+
				   "WHERE title LIKE %:kw% "+
				   "OR content LIKE %:kw%",
				   nativeQuery = true)
	List<PlatformAnnouncementVO> findByKeyword(@Param("kw")String keyword);
	
	// 分頁 ＋ 模糊查詢（不分大小寫）
	Page<PlatformAnnouncementVO> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
			String titleKeyword, String contentKeyword, Pageable pageable);

}
