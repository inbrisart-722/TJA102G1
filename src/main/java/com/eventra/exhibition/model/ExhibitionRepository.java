package com.eventra.exhibition.model;

import java.time.LocalDateTime;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

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

	 /** 
	  * peichenlu
	  * [收藏展覽通知(event notification) - 開賣提醒]
	  * 查詢 即將開賣 的展覽, 開賣時間在 now ~ until 之間
	  *
	  */
	 @Query("SELECT e FROM ExhibitionVO e " +
		       "WHERE e.ticketStartTime IS NOT NULL " +
		       "AND e.ticketStartTime BETWEEN :now AND :until")
	 List<ExhibitionVO> findExhibitionsStartingWithin(@Param("now") LocalDateTime now, @Param("until") LocalDateTime until);
	 
	 Slice<ExhibitionVO> findByStartTimeAfter(LocalDateTime now, Pageable pageable);
}
