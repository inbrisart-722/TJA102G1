package com.eventra.comment_reaction.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.comment.model.CommentRepository;
import com.eventra.comment.model.CommentVO;
import com.eventra.member.model.MemberVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CommentReactionService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private final CommentReactionRepository COMMENT_REACTION_REPO;
	private final CommentRepository COMMENT_REPO;
	
	public CommentReactionService(CommentReactionRepository commentReactionRepository, CommentRepository commentRepository) {
		this.COMMENT_REACTION_REPO = commentReactionRepository;
		this.COMMENT_REPO = commentRepository;
	}
	
	@Transactional
	public UpdateCommentReactionResDTO updateCommentReaction(UpdateCommentReactionReqDTO req, Integer memberId) {
		String currentReaction = null;
		
		Integer commentId = req.getCommentId();
		String reaction = req.getReaction();
		
		CommentReactionVO commentReactionVOOriginal = COMMENT_REACTION_REPO.findByComment_CommentIdAndMember_MemberId(commentId, memberId).orElse(null);
		String reactionOriginal = (commentReactionVOOriginal != null ? commentReactionVOOriginal.getReaction() : null);
		
		// 6 種情況
		if(reactionOriginal == null) {
			// 1 此留言 此會員 沒有按讚／倒讚紀錄 => save new
			CommentReactionVO commentReactionVONew = new CommentReactionVO.Builder()
					.comment(entityManager.getReference(CommentVO.class, commentId))
					.member(entityManager.getReference(MemberVO.class, memberId))
					.reaction(reaction)
					.build();
			
			COMMENT_REACTION_REPO.save(commentReactionVONew);
			if("LIKE".equals(reaction)) COMMENT_REPO.updateLikeCount(commentId, 1);
			else if("DISLIKE".equals(reaction)) COMMENT_REPO.updateDislikeCount(commentId, 1);
			currentReaction = reaction;
		} else if (reactionOriginal.equals(reaction)){
			// 2 此留言 此會員 有按讚／倒讚紀錄 => delete original
			COMMENT_REACTION_REPO.delete(commentReactionVOOriginal);
			if("LIKE".equals(reaction))	COMMENT_REPO.updateLikeCount(commentId, -1);
			else if("DISLIKE".equals(reaction)) COMMENT_REPO.updateDislikeCount(commentId, -1);
			// currentReaction 仍保持 null 即可
		} else if (!(reactionOriginal.equals(reaction))) {
			// 3 此留言 此會員 有按讚／倒讚紀錄 且 這次動作還相反 => delete original + flush + save new => 改成只要 update 即可
//			COMMENT_REACTION_REPO.delete(commentReactionVOOriginal);
//			entityManager.flush();
//			COMMENT_REACTION_REPO.save(commentReactionVONew);
			if("LIKE".equals(reaction)) {
				COMMENT_REPO.updateDislikeCount(commentId, -1);
				COMMENT_REPO.updateLikeCount(commentId, 1);
				commentReactionVOOriginal.setReaction("LIKE");
			}
			else if("DISLIKE".equals(reaction)) {
				COMMENT_REPO.updateLikeCount(commentId, -1);
				COMMENT_REPO.updateDislikeCount(commentId, 1);
				commentReactionVOOriginal.setReaction("DISLIKE");
			}
			currentReaction = reaction;
		}
		
		CommentVO vo = COMMENT_REPO.findById(commentId).orElseThrow();
		UpdateCommentReactionResDTO res = new UpdateCommentReactionResDTO()
				.setStatus("success")
				.setCurrentReaction(currentReaction)
				.setLikeCount(vo.getLikeCount())
				.setDislikeCount(vo.getDislikeCount());
		return res;
	}
}
