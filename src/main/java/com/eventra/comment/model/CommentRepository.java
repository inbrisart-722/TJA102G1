package com.eventra.comment.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventra.comment.controller.CommentStatus;

public interface CommentRepository extends JpaRepository<CommentVO, Integer>{
	
	// 父留言 => 找該展覽 comments + replies 總數
//	@Query(value = "select count(c) from CommentVO c where c.commentStatus = :cs and c.exhibition.exhibitionId = :eid")
//	Integer findCountByExhibition(@Param("cs") CommentStatus commentStatus, @Param("eid") Integer exhibitionId);
	
	// 子留言 => 找該展覽 該留言 replies 總數
	@Query(value = "select count(c) from CommentVO c where c.commentStatus = :cs and c.exhibition.exhibitionId = :eid and c.parentComment.commentId = :pcid")
	Integer findCountByParent(@Param("cs") CommentStatus commentStatus, @Param("eid") Integer exhibitionId, @Param("pcid") Integer parentCommentId);
	
	// 父留言 => 找該展覽 comments by offset
	@Query(value = "select c from CommentVO c where c.commentStatus = :cs and c.exhibition.exhibitionId = :eid and c.parentComment is null and (:cid is null or c.commentId < :cid) order by c.commentId desc")
	Slice<CommentVO> findComments(@Param("cs") CommentStatus commentStatus, @Param("eid") Integer exhibitionId, @Param("cid") Integer commentId, Pageable pageable);
	
	// 子留言 => 找該展覽 該留言 replies by offset
	@Query(value = "select c from CommentVO c where c.commentStatus = :cs and c.exhibition.exhibitionId = :eid and c.parentComment.commentId = :pcid and (:cid is null or c.commentId < :cid) order by c.commentId desc")
	Slice<CommentVO> findCommentsByParent(@Param("cs") CommentStatus commentStatus, @Param("eid") Integer exhibitionId, @Param("pcid") Integer parentCommentId, @Param("cid") Integer commentId, Pageable pageable);
	
//	@Transactional // 交給 CommentReactionService 控制
	@Modifying
	@Query(value = "update CommentVO c set c.likeCount = c.likeCount + :delta where c.commentId = :cid")
	Integer updateLikeCount(@Param("cid") Integer commentId, @Param("delta") Integer delta);
	
//	@Transactional // 交給 CommentReactionService 控制
	@Modifying
	@Query(value = "update CommentVO c set c.dislikeCount = c.dislikeCount + :delta where c.commentId = :cid")
	Integer updateDislikeCount(@Param("cid") Integer commentId, @Param("delta") Integer delta);
	
	@Query(value = "select count(*) from comment c "
			+ "where c.exhibition_id = :eid "
			+ "and ( ( c.comment_status = :cs and c.parent_comment_id is null ) or "
			+ "	     ( c.comment_status = :cs and c.parent_comment_id is not null and exists (select 1 from comment p where p.comment_id = c.parent_comment_id and p.comment_status = '正常') ) "
			+ "	   )"
			, nativeQuery = true)
	Integer countByExhibitionId(@Param("cs") String commentStatus, @Param("eid") Integer exhibitionId);
	// nativeQuery 不能 Enum
}
