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
	
	Page<ExhibitorVO> findByReviewStatusIdIn(Set<Integer> statuses, Pageable pageable);
}
