package com.eventra.comment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.comment.model.AddCommentReqDTO;
import com.eventra.comment.model.AddCommentResDTO;
import com.eventra.comment.model.CommentRepository;
import com.eventra.comment.model.CommentService;
import com.eventra.comment.model.LoadCommentReqDTO;
import com.eventra.comment.model.LoadCommentResDTO;

@RestController
@RequestMapping("/api/comment")
public class CommentRestController {

	private final CommentService COMMENT_SERVICE;
	private static final Integer TEST_MEMBER = 3;
	
	public CommentRestController(CommentService commentService) {
		this.COMMENT_SERVICE = commentService;
	}
	
	// encoding ... listener
	
	@PostMapping("addComment")
	public AddCommentResDTO addComment(@RequestBody AddCommentReqDTO req) {
		return COMMENT_SERVICE.addComment(req, TEST_MEMBER);
	}
	// 前端給：exhibitionId, parentCommentId(nullable), content => addCommentReqDTO
	// 後端回：status, commentVO, commentCount, replyCount => addCommentResDTO
	
	@PostMapping("loadComment")
	public LoadCommentResDTO loadComment(@RequestBody LoadCommentReqDTO req) {
		return COMMENT_SERVICE.loadComment(req, TEST_MEMBER);
	}
	// 前端給：exhibitionId, created_at, comment_id, parentCommentId(nullable) => loadCommentReqDTO
	// 後端回：status, message, list, mapReaction, hasNextPage => loadCommentResDTO
		
//	@PostMapping("updateCommentStatus")
//	@GetMapping("getComment") // 平台用
}
