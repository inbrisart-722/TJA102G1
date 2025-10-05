package com.eventra.exhibitionreviewlog.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("exhibitionReviewLogRepository")
public interface ExhibitionReviewLogRepository extends JpaRepository<ExhibitionReviewLogVO, Integer> {

}
