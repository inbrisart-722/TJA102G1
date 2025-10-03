package com.eventra.exhibition.model;


import org.springframework.data.domain.Page;
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

	// 草稿查詢
	@Query("""
			select e from ExhibitionVO e
			where e.exhibitorVO.exhibitorId = :exhibitorId
			and e.exhibitionStatusId = :draftId
			and (:q is null or :q = '' or lower(e.exhibitionName) like lower(concat('%', :q, '%')))
			""")
	Page<ExhibitionVO> findDrafts(@Param("exhibitorId") Integer exhibitorId, 
								  @Param("draftId") Integer draftId,
								  @Param("q") String q,
								  Pageable pageable);

	// 尚未開賣查詢：尚未到 ticketStartTime 且尚未結束
	@Query("""
			select e from ExhibitionVO e
			where e.exhibitorVO.exhibitorId = :exhibitorId
			and e.exhibitionStatusId <> :draftId
			and (e.endTime is null or e.endTime > CURRENT_TIMESTAMP)
			and (e.ticketStartTime is not null and e.ticketStartTime > CURRENT_TIMESTAMP)
			and (:q is null or :q = '' or lower(e.exhibitionName) like lower(concat('%', :q, '%')))
			""")
	Page<ExhibitionVO> findNotOnSale(@Param("exhibitorId") Integer exhibitorId, 
									 @Param("draftId") Integer draftId,
									 @Param("q") String q,
									 Pageable pageable);

	// 售票中：已到開賣時間，且未結束
	@Query("""
			select e from ExhibitionVO e
			where e.exhibitorVO.exhibitorId = :exhibitorId
			and e.exhibitionStatusId <> :draftId
			and (e.ticketStartTime <= CURRENT_TIMESTAMP and CURRENT_TIMESTAMP < e.endTime)
			and (:q is null or :q = '' or lower(e.exhibitionName) like lower(concat('%', :q, '%')))
			""")
	Page<ExhibitionVO> findOnSale(@Param("exhibitorId") Integer exhibitorId, 
								  @Param("draftId") Integer draftId,
								  @Param("q") String q,
								  Pageable pageable);

	// 已結束：已過 endTime
	@Query("""
			select e from ExhibitionVO e
			where e.exhibitorVO.exhibitorId = :exhibitorId
			and e.exhibitionStatusId <> :draftId
			and e.endTime < CURRENT_TIMESTAMP
			and (:q is null or :q = '' or lower(e.exhibitionName) like lower(concat('%', :q, '%')))
			""")
	Page<ExhibitionVO> findEnded(@Param("exhibitorId") Integer exhibitorId, 
								 @Param("draftId") Integer draftId,
								 @Param("q") String q,
								 Pageable pageable);

	// 全部（分頁）
	Page<ExhibitionVO> findByExhibitorVO_ExhibitorIdAndExhibitionNameContainingIgnoreCase(Integer exhibitorId, String exhibitionName, Pageable pageable);

	// 查詢審核狀態 
	Page<ExhibitionVO> findByExhibitorVO_ExhibitorIdAndExhibitionStatusIdAndExhibitionNameContainingIgnoreCase(
	        Integer exhibitorId, Integer exhibitionStatusId, String nameKeyword, Pageable pageable);
	
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
	 
	 // 暫時不判斷 3, 4
	 Slice<ExhibitionVO> findByStartTimeAfter(LocalDateTime now, Pageable pageable);

	  // 找最近展覽
	    @Query(value = """
	        SELECT e.*, (
	            6371000 * acos(
	                cos(radians(:lat)) * cos(radians(e.latitude)) * cos(radians(e.longitude) - radians(:lng))
	              + sin(radians(:lat)) * sin(radians(e.latitude))
	            )
	        ) AS distance,
	        (SELECT AVG(r.rating_score) 
	    		 FROM rating r 
	    		 WHERE r.exhibition_id = e.exhibition_id)
	    	  AS averageRatingScore
	        FROM exhibition e
	        WHERE e.exhibition_status_id in (3,4) and e.latitude is not null and e.longitude is not null
	        ORDER BY distance
	        """, nativeQuery = true)
	    Slice<ExhibitionVO> findNearestExhibition(@Param("lat") Double lat, @Param("lng") Double lng, Pageable pageable);
	    
	    @Query("SELECT e FROM ExhibitionVO e WHERE e.exhibitionStatusId in (3,4) AND ( (e.averageRatingScore < :score) OR "
	    		+ " (e.averageRatingScore = :score AND e.exhibitionId > :eid) ) ORDER BY e.averageRatingScore DESC, e.exhibitionId ASC")
	    Slice<ExhibitionVO> findExhibitionsByAverageRatingScoreDesc(@Param("score") Double score, @Param("eid") Integer exhibitionId, Pageable pageable);
	    
	  /* 防止超賣，當訂單狀態為已付款時，總票數扣除已販售票數大於等於訂單購買票數時才可更新已販售票數之數量，
	             */    		 
	    @Modifying
	    @Query("""
	    		update ExhibitionVO e
	    			set e.soldTicketQuantity = coalesce(e.soldTicketQuantity, 0) + :delta
	    		where e.exhibitionId = :exhibitionId
	    		    and(coalesce(e.totalTicketQuantity, 0)) - (coalesce(e.soldTicketQuantity, 0)) >= :delta
	    		""")
	    
	    int tryIncreaseSold(@Param("exhibitionId") Integer exhibitionId,
	    					@Param("delta") int delta);
	    
	    @Modifying
	    @Query("""
	    		update ExhibitionVO e
	    		    set e.soldTicketQuantity = greatest(coalesce(e.soldTicketQuantity,0) - :delta, 0)
	    		where e.exhibitionId = :exhibitionId
	    """)
	    int decreaseSold(@Param("exhibitionId") Integer exhibitionId,
	                     @Param("delta") int delta);
}
