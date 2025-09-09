package com.eventra.search.model;

import java.sql.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<SearchVO, Integer> {

    // 查詢當天是否已有該會員的該關鍵字
    Optional<SearchVO> findByMemberIdAndKeywordAndSearchedAt(
            Integer memberId, String keyword, Date searchedAt);
}
