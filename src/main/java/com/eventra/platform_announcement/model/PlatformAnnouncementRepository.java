package com.eventra.platform_announcement.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface PlatformAnnouncementRepository extends JpaRepository<PlatformAnnouncementVO, Integer> {

	// 模糊查詢
	@Query(value = "SELECT * FROM platform_announcement "+
				   "WHERE title LIKE %:kw% "+
				   "OR content LIKE %:kw%",
				   nativeQuery = true)
	List<PlatformAnnouncementVO> findByKeyword(@Param("kw")String keyword);
	
	

}
