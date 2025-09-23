package com.eventra.customerservice.model;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceService {

	private final CustomerServiceRedisRepository CS_REPO;
	
	public CustomerServiceService(CustomerServiceRedisRepository customerServiceRedisRepository) {
		this.CS_REPO = customerServiceRedisRepository;
	}
	
	public ChatMessageResDTO addMessage(ChatMessageReqDTO req, Integer memberId) {
		// 1. req to res
				
		ChatMessageResDTO res = new ChatMessageResDTO();
		res.setContent(req.getContent())
			.setSentTime(System.currentTimeMillis())
			.setAvatarSrc("img/group1_img/aa志嘉.png") // testing
			.setMemberId(memberId);
		
		// 2. 呼叫 repo
		CS_REPO.addMessage(res);
		
		return res;
	}
	
	public List<ChatMessageResDTO> getMessages(Long timestamp){
		return CS_REPO.getMessages(timestamp);
	}
	
}
