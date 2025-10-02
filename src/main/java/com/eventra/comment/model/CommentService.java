package com.eventra.comment.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.comment.controller.CommentStatus;
import com.eventra.comment_reaction.model.CommentReactionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.member.model.MemberVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional(readOnly = true)
public class CommentService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private final CommentRepository COMMENT_REPO;
	private final CommentReactionRepository COMMENT_REACTION_REPO;
	
	private final PageRequest PAGE_LOAD = PageRequest.of(0, 3); // Sort.by("commentId").descending() 寫死在 Repo JPQL
	
	private final String DEFAULT_PROFILE_PIC;
	
	public CommentService(CommentRepository commentRepository, CommentReactionRepository commentReactionRepository, @Value("${default.profile-pic}") String defaultProfilePic) {
		this.COMMENT_REPO = commentRepository;
		this.COMMENT_REACTION_REPO = commentReactionRepository;
		this.DEFAULT_PROFILE_PIC = defaultProfilePic;
	}
	
	@Transactional
	public AddCommentResDTO addComment(AddCommentReqDTO req, Integer memberId) {
		// 1. reqDTO => VO
		// 透過 entityManager.getReference 建立只含 id 的 proxy，不會 hit DB
		// EntityNotFoundException, IllegalArgumentException
        ExhibitionVO exhibitionRef = entityManager.getReference(ExhibitionVO.class, req.getExhibitionId());
        MemberVO memberRef = entityManager.getReference(MemberVO.class, memberId);
        // 透過 entityManager.getReference 建立只含 id 的 proxy，不會 hit DB
        CommentVO parentCommentRef = null;
        if (req.getParentCommentId() != null) {
            parentCommentRef = entityManager.getReference(CommentVO.class, req.getParentCommentId());
        }
        
        // 用 Builder 組 CommentVO
        CommentVO commentVOIn = new CommentVO.Builder()
                .exhibition(exhibitionRef)
                .member(memberRef)
                .parentComment(parentCommentRef)
                .content(req.getContent())
                .build();

        // 2. VOIn => VOOut => dto
        // resDTO 參數 0 (status0
        // resDTO 參數 1 (commentVOOut -> commentDTO)
        CommentVO commentVOOut = COMMENT_REPO.saveAndFlush(commentVOIn); // 送去DB先執行（交易尚未提交） => 1. 與交易本身提交是兩回事 
        entityManager.refresh(commentVOOut); // 去DB查詢後更新快取 => 1. 拿到其他自增值 2. 也可用 @CreationTimestamp, @UpdateTimestamp 就不用 refresh
        
        // resDTO 參數 1-1 (member)
        MemberVO member = commentVOOut.getMember();
        CommentDTO.Member cm = new CommentDTO.Member();
        String profilePic = member.getProfilePic() != null ? member.getProfilePic() : DEFAULT_PROFILE_PIC;
        cm.setMemberId(member.getMemberId())
        .setNickname(member.getNickname())
        .setProfilePic(profilePic);
        
        // resDTO 參數 1-1 (member) 塞入 1-2 (commentDTO)
        CommentDTO cdto = new CommentDTO();
        cdto.setCommentId(commentVOOut.getCommentId())
        	.setMember(cm)
        	.setContent(commentVOOut.getContent())
        	.setCreatedAt(commentVOOut.getCreatedAt())
        	.setLikeCount(commentVOOut.getLikeCount())
        	.setDislikeCount(commentVOOut.getDislikeCount())
        	.setChildCommentsCount(commentVOOut.getChildCommentsCount());
        
        
        // resDTO 參數 3 (commentCount)
        Integer commentCount = COMMENT_REPO.countByExhibitionId(CommentStatus.正常.toString(), req.getExhibitionId());
        // resDTO 參數 4 (replyCount)
        Integer replyCount = (parentCommentRef != null) ? COMMENT_REPO.findCountByParent(CommentStatus.正常, req.getExhibitionId(), req.getParentCommentId()) : null;
        
        // 3. VO => resDTO
        AddCommentResDTO res = new AddCommentResDTO().setStatus("success").setComment(cdto).setCommentCount(commentCount).setReplyCount(replyCount);
        
        return res;
	}
	
	public LoadCommentResDTO loadComment(LoadCommentReqDTO req, Integer memberId) {
		// 1. reqDTO => 取值
		Integer exhibitionId = req.getExhibitionId();
		Integer parentCommentId = req.getParentCommentId();
		Integer commentId = req.getCommentId();
		
		// 2. reqDTO => Slice<VO>
		Slice<CommentVO> slice = (parentCommentId == null)
				? COMMENT_REPO.findComments(CommentStatus.正常, exhibitionId, commentId, PAGE_LOAD)
				: COMMENT_REPO.findCommentsByParent(CommentStatus.正常, exhibitionId, parentCommentId, commentId, PAGE_LOAD);
		
		// 3. Slice<VO> => List<resDTO>
		// resDTO 參數1
		List<CommentVO> voList = slice.getContent();
		// 手動轉吧
		List<CommentDTO> dtoList = new ArrayList<>();
		
		for(CommentVO vo : voList) {
			MemberVO member = vo.getMember();
			// 1. build CommentDTO.Member
			CommentDTO.Member cm = new CommentDTO.Member();
			String profilePic = member.getProfilePic() != null ? member.getProfilePic() : DEFAULT_PROFILE_PIC;
			cm.setMemberId(member.getMemberId())
				.setNickname(member.getNickname())
				.setProfilePic(profilePic);
			// 2. build CommentDTO
			CommentDTO dto = new CommentDTO();
			dto.setCommentId(vo.getCommentId())
				.setContent(vo.getContent())
				.setCreatedAt(vo.getCreatedAt())
				.setLikeCount(vo.getLikeCount())
				.setDislikeCount(vo.getDislikeCount())
				.setChildCommentsCount(vo.getChildCommentsCount())
				.setMember(cm);
			// 3. insert into the dtoList
			dtoList.add(dto);
		}
		
		// resDTO 參數2
		boolean hasNextPage = slice.hasNext();
		// resDTO 參數3 (voList -> idList -> reactionList -> mapReaction(idList + reactionList) )
		List<Integer> idList = voList.stream().map(CommentVO :: getCommentId).collect(Collectors.toList());
		
		// 截斷
		if(memberId == null) return new LoadCommentResDTO().setStatus("guest").setList(dtoList).setHasNextPage(hasNextPage);
		
		List<Object[]> idReactionList = COMMENT_REACTION_REPO.findReactionsByMember(memberId, idList); 
				// 自訂@Query 只能帶 Pageable 不能直接帶 Sort => 乾脆用 order by 寫死本來的 Sort.by("comment.commentId").descending()
		Map<Integer, String> mapReaction = new HashMap<>();
		for(Object[] el : idReactionList) {
			Integer id = (Integer)el[0];
			String reaction = (String)el[1];
			mapReaction.put(id, reaction);
		}
		
		LoadCommentResDTO res = new LoadCommentResDTO().setStatus("member").setList(dtoList).setHasNextPage(hasNextPage).setMapReaction(mapReaction);
		return res;
	}
}
//front-end
//一般登入
	// ssr -> [SecurityConfig] defaultAuthenticationEntryPointfor 
		// target = "/front-end/login?redirect=" + req.getRequestURI()
		// if(req.getQueryString() != null) target += "&" + req.getQueryString();
		// res.sendRedirect(target); -> "/front-end/login?redirect=exhibitions&exhibitionId=3"
	// api -> [任一 .js 檔 ajax] 401 帶入 sessionStorage ("redirect" = pathname + search) -> [g1_10_custom_login.js] 導回
//oauth2 登入
	// ssr -> [SecurityConfig] oauth2Login successHandler
		// SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response)
		// String targetUrl = savedRequest.getRedirectUrl();
		// response.sendRedirect(targetUrl);
	// csr -> ????