package com.eventra.exhibitiontickettype.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionTicketTypeRepository extends JpaRepository<ExhibitionTicketTypeVO, Integer>{
 // 拿整顆 VO -> derived query 派生查詢
 Optional<ExhibitionTicketTypeVO> findByExhibitionIdAndTicketTypeId(Integer exhibitionId, Integer ticketTypeId);
 
 // 只拿 Id -> 自訂 Query
// @Query("select e.exhibitionTicketTypeId from ExhibitionTicketTypeVO e where e.exhibitionId = :eid and e.ticketTypeId = :ttid")
//  Integer findIdByExhibitionIdAndTicketTypeId(@Param("eid") Integer exhibitionId, @Param("ttid")Integer ticketTypeId);
 
 // 1. 融合在以下
// @Query("select e.exhibitionId from ExhibitionTicketTypeVO e where e.exhibitionTicketTypeId = :ettid")
// Integer findExhibitionId(@Param("ettid") Integer exhibitionTicketTypeId);
 
 // 2. 乾脆用派生
 @Query("select e.exhibitionId from ExhibitionTicketTypeVO e where e.exhibitionTicketTypeId in (:ettids)")
 List<Integer> findExhibitionId(@Param("ettids") Collection<Integer> exhibitionTicketTypeIds);
 
 // 1. 和 2. 的結局 ...
 // 3. 派生不能選欄位......
// List<Integer> findExhibitionIdByExhibitionTicketTypeIdIn(Collection<Integer> ids);
 
}