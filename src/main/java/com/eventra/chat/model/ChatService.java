package com.eventra.chat.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.member.model.MemberRepository;
import com.eventra.member.model.MemberVO;

@Service
@Transactional
public class ChatService {

	private final ChatRedisRepository CS_REPO;
	private final MemberRepository MEMBER_REPO;
	
	private final String DEFAULT_PROFILE_PIC;
	
	public ChatService(ChatRedisRepository customerServiceRedisRepository, MemberRepository memberRepository, @Value("${default.profile-pic}") String defaultProfilePic) {
		this.CS_REPO = customerServiceRedisRepository;
		this.MEMBER_REPO = memberRepository;
		this.DEFAULT_PROFILE_PIC = defaultProfilePic;
	}
	
	public ChatMessageResDTO addMessage(ChatMessageReqDTO req, Integer memberId) {
		// 1. req to res
		MemberVO member = MEMBER_REPO.findById(memberId).orElse(null);
		String profilePic = member != null ? member.getProfilePic() : null;
		if(profilePic == null) profilePic = DEFAULT_PROFILE_PIC;
				
		ChatMessageResDTO res = new ChatMessageResDTO();
		res.setContent(req.getContent())
			.setSentTime(System.currentTimeMillis())
			.setAvatarSrc(profilePic)
			.setMemberId(memberId);
		
		// 2. 呼叫 repo
		CS_REPO.addMessage(res);
		
		return res;
	}
	
	public List<ChatMessageResDTO> getMessages(Long timestamp){
		return CS_REPO.getMessages(timestamp);
	}
	
}
