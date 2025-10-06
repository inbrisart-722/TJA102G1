package com.eventra.exhibitor.model;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitorRepository extends JpaRepository<ExhibitorVO, Integer> {
	
	Optional<ExhibitorVO> findByBusinessIdNumber(String businessIdNumber);
	
	boolean existsByBusinessIdNumber(String businessIdNumber);
	boolean existsByEmail(String email);

	// 忽略大小寫比對
	Optional<ExhibitorVO> findByEmailIgnoreCase(String email);
	
	// 平台公告用, 根據審核狀態 ID 統計數量
	long countByReviewStatusId(Integer reviewStatusId);
	
	Page<ExhibitorVO> findByReviewStatusIdIn(Set<Integer> statuses, Pageable pageable);
}
