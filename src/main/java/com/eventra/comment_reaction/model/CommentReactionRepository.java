package com.eventra.comment_reaction.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentReactionRepository extends JpaRepository<CommentReactionVO, Integer>{

	// loadComment 載入此會員過去的按讚、倒讚紀錄時使用
	@Query(value = "select cr.comment.commentId, cr.reaction from CommentReactionVO cr where cr.member.memberId = :mid and cr.comment.commentId in (:cids) order by cr.comment.commentId desc")
	List<Object[]> findReactionsByMember(@Param("mid") Integer memberId, @Param("cids") List<Integer> commentIds);
	
	// updateCommentReaction 更動會員按讚、倒讚紀錄時使用
	// 直接使用 derived query
	Optional<CommentReactionVO> findByComment_CommentIdAndMember_MemberId(Integer commentId, Integer memberId);
	
	// archive
//	@Query(value = "select cr from CommentReactionVO cr where cr.comment.commentId = :cid and cr.member.memberId = :mid")
//	CommentReactionVO findOriginalReactionByMember(@Param("cid") Integer commentId, @Param("mid") Integer memberId);
}
