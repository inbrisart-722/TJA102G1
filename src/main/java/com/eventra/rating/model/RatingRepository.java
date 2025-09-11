package com.eventra.rating.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<RatingVO, Integer> {

 @Query(value = "select r.ratingScore from RatingVO r where r.exhibition.exhibitionId = :eid and r.member.memberId = :mid")
 Byte getRatingScore(@Param("eid") Integer exhibitionId, @Param("mid") Integer memberId);
 
 @Query(value = "select r from RatingVO r where r.exhibition.exhibitionId = :eid and r.member.memberId = :mid")
 RatingVO getRating(@Param("eid") Integer exhibitionId, @Param("mid") Integer memberId);
 
}