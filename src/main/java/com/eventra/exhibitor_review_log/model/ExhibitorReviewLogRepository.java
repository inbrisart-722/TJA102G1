package com.eventra.exhibitor_review_log.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("exhibitorReviewLogRepository")
public interface ExhibitorReviewLogRepository extends JpaRepository<ExhibitorReviewLogVO, Integer> {

}
