package com.eventra.exhibitor.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository; // 可選

// @Repository // 可選：Spring Data 會自動偵測介面
public interface ExhibitorRepository extends JpaRepository<ExhibitorVO, Integer> {

    // 依統一編號查（建議 DB 對 business_id_number 加 UNIQUE 索引）
    Optional<ExhibitorVO> findByBusinessIdNumber(String businessIdNumber);

    // 單一狀態
    List<ExhibitorVO> findByReviewStatusId(Integer reviewStatusId);

    // 多個狀態（例如 List.of(1, 3)）
    List<ExhibitorVO> findByReviewStatusIdIn(Collection<Integer> reviewStatusIds);
}
