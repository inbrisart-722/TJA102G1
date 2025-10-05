package com.eventra.exhibition_review_log.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("exhibitionReviewLogRepository")
public interface ExhibitionReviewLogRepository extends JpaRepository<ExhibitionReviewLogVO, Integer> {

	Optional<ExhibitionReviewLogVO> findTopByExhibitionIdOrderByReviewedAtDesc(Integer exhibitionId);

	
}
