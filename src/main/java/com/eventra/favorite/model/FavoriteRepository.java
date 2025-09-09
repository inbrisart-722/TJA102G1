package com.eventra.favorite.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface FavoriteRepository extends JpaRepository<FavoriteVO, Integer> {

	// 修改收藏狀態
	@Transactional
	@Modifying
	@Query(value = "UPDATE favorite " +
	               "SET favorite_status = :status, updated_at = NOW() " +
	               "WHERE favorite_id = :favId",
	               nativeQuery = true)
	int updateStatus(@Param("status") Integer favStatus, @Param("favId") Integer favId);
	
	// 查詢會員是否已有收藏展覽 (UK: memberId + exhibitionId)
	Optional<FavoriteVO> findByMemberIdAndExhibitionId(Integer memId, Integer exhId);
	
	// 查詢會員所有收藏 (status = 1)
	@Query(value = "SELECT * FROM favorite " +
				   "WHERE member_id = :memId " +
				   "AND favorite_status = 1 " +
				   "ORDER BY favorite_id DESC",
	               nativeQuery = true)
	List<FavoriteVO> findFavoritesByMember(@Param("memId") Integer memId);
}
