package com.eventra.customerservice.model;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.member.model.MemberRepository;
import com.eventra.member.model.MemberVO;

@Service
@Transactional
public class CustomerServiceService {

	private final CustomerServiceRedisRepository CS_REPO;
	private final MemberRepository MEMBER_REPO;
	
	public CustomerServiceService(CustomerServiceRedisRepository customerServiceRedisRepository, MemberRepository memberRepository) {
		this.CS_REPO = customerServiceRedisRepository;
		this.MEMBER_REPO = memberRepository;
	}
	
	public ChatMessageResDTO addMessage(ChatMessageReqDTO req, Integer memberId) {
		// 1. req to res
		MemberVO member = MEMBER_REPO.findById(memberId).orElse(null);
		String profilePic = member != null ? member.getProfilePic() : "img/group1_img/aa志嘉.png";
				
		ChatMessageResDTO res = new ChatMessageResDTO();
		res.setContent(req.getContent())
			.setSentTime(System.currentTimeMillis())
			.setAvatarSrc(profilePic) // testing
			.setMemberId(memberId);
		
		// 2. 呼叫 repo
		CS_REPO.addMessage(res);
		
		return res;
	}
	
	public List<ChatMessageResDTO> getMessages(Long timestamp){
		return CS_REPO.getMessages(timestamp);
	}
	
}
