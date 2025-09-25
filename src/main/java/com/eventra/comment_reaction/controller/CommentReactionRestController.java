package com.eventra.comment_reaction.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.comment_reaction.model.CommentReactionService;
import com.eventra.comment_reaction.model.UpdateCommentReactionReqDTO;
import com.eventra.comment_reaction.model.UpdateCommentReactionResDTO;

@RestController
@RequestMapping("api/front-end/protected/commentReaction")
public class CommentReactionRestController {
	
	private final CommentReactionService COMMENT_REACTION_SERVICE;
//	private static final Integer TEST_MEMBER = 3;
	
	public CommentReactionRestController(CommentReactionService commentReactionService) {
		this.COMMENT_REACTION_SERVICE = commentReactionService;
	}
	
	@PostMapping("/updateReaction")
	public UpdateCommentReactionResDTO updateReaction(@RequestBody UpdateCommentReactionReqDTO req, Principal principal) {
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		if(memberId == null) return null;
		else return COMMENT_REACTION_SERVICE.updateCommentReaction(req, memberId);
	}
	// 前端給：commentId, reaction => UpdateCommentReactionReqDTO
	// 後端回：status, currentReaction, likeCount, dislikeCount} => UpdateCommentReactionResDTO
}
