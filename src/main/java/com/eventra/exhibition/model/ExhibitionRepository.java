package com.eventra.exhibition.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionRepository extends JpaRepository<ExhibitionVO, Integer> {

	// @Transactional 給 service 包，要記得
	@Modifying
	@Query(value = "update ExhibitionVO e set e.totalRatingCount = e.totalRatingCount + :delta where e.exhibitionId = :eid")
	Integer updateTotalRatingCount(@Param("eid") Integer exhibitionid, @Param("delta") Integer delta);

	// @Transactional 給 service 包，要記得
	@Modifying
	@Query(value = "update ExhibitionVO e set e.totalRatingScore = e.totalRatingScore + :delta where e.exhibitionId = :eid")
	Integer updateTotalRatingScore(@Param("eid") Integer exhibitionid, @Param("delta") Byte delta);

	@Modifying
	@Query(value = "update ExhibitionVO e set e.soldTicketQuantity = e.soldTicketQuantity + :delta where e.exhibitionId = :eid "
			+ "and e.soldTicketQuantity + :delta <= e.totalTicketQuantity")
	Integer updateSoldTicketQuantity(@Param("eid") Integer exhibitionId, @Param("delta") Integer delta);

	// 草稿查詢
	@Query("""
			select e from ExhibitionVO e
			where e.exhibitorVO.exhibitorId = :exhibitorId
			and e.exhibitionStatusId = :draftId
			""")
	Page<ExhibitionVO> findDrafts(@Param("exhibitorId") Integer exhibitorId, @Param("draftId") Integer draftId,
			Pageable pageable);

	// 尚未開賣查詢：尚未到 ticketStartTime 且尚未結束
	@Query("""
			select e from ExhibitionVO e
			where e.exhibitorVO.exhibitorId = :exhibitorId
			and (e.endTime is null or e.endTime > CURRENT_TIMESTAMP)
			and (e.ticketStartTime is not null and e.ticketStartTime > CURRENT_TIMESTAMP)
			""")
	Page<ExhibitionVO> findNotOnSale(@Param("exhibitorId") Integer exhibitorId, Pageable pageable);

	// 售票中：已到開賣時間，且未結束
	@Query("""
			select e from ExhibitionVO e
			where e.exhibitorVO.exhibitorId = :exhibitorId
			and (e.ticketStartTime <= CURRENT_TIMESTAMP and CURRENT_TIMESTAMP < e.endTime)
			""")
	Page<ExhibitionVO> findOnSale(@Param("exhibitorId") Integer exhibitorId, Pageable pageable);

	// 已結束：已過 endTime
	@Query("""
			select e from ExhibitionVO e
			where e.exhibitorVO.exhibitorId = :exhibitorId
			and e.endTime < CURRENT_TIMESTAMP
			""")
	Page<ExhibitionVO> findEnded(@Param("exhibitorId") Integer exhibitorId, Pageable pageable);

	// 全部（分頁）
	Page<ExhibitionVO> findByExhibitorVO_ExhibitorId(Integer exhibitorId, Pageable pageable);
}
