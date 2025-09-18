package com.eventra.member.model;

import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.member.model.MemberVO;

@Service
@Transactional
public class MemberService {
	
	private final MemberRepository MEMBER_REPO;
	private final MemberRedisRepository MEMBER_REDIS_REPO;
	private final BCryptPasswordEncoder PASSWORD_ENCODER; 
	
	public MemberService(MemberRepository memberRepository, MemberRedisRepository memberRedisRepository, BCryptPasswordEncoder passwordEncoder) {
		this.MEMBER_REPO = memberRepository;
		this.MEMBER_REDIS_REPO = memberRedisRepository;
		this.PASSWORD_ENCODER = passwordEncoder;
	}
	
	public boolean checkIfMember(String email) {
		MemberVO memberVO = MEMBER_REPO.findByEmail(email).orElse(null);
		if(memberVO == null) return false; // 是會員（已經在會員 DB 中）
		else return true; // 不是會員
	}
	
	public String register(RegisterReqDTO req) {
		// const send_data = { token , password, nickname };
		String token = req.getToken();
		
		String password = req.getPassword();
		String nickname = req.getNickname();
		String email = MEMBER_REDIS_REPO.findEmailByToken(token);
		if(email == null) return null;
		
		String password_hash = PASSWORD_ENCODER.encode(password);
		
		MemberVO member = new MemberVO.Builder(email, password_hash, nickname).build();
		MEMBER_REPO.save(member);
		
		MEMBER_REDIS_REPO.deleteToken(token);
		return email;
	}
	
	public void updateMemberPhoto() {
		
	}
	public void updateMemberInfo(UpdateInfoReqDTO req) {
		// 1. 從 SecurityContext 拿 Authentication
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// 2. 從 Authenticaiton 拿 name (member的情況下是email)
		String email = auth.getName();
		// 3. 從 email 拿 member 物件
		MemberVO member = MEMBER_REPO.findByEmail(email).orElseThrow();
		// 4. 把更新資料塞入 managed object (因為 @Transactional）
		if(!Objects.equals(member.getNickname(), req.getNickname())) member.setNickname(req.getNickname());
		if(!Objects.equals(member.getPhoneNumber(), req.getPhoneNumber())) member.setPhoneNumber(req.getPhoneNumber());
		if(!Objects.equals(member.getBirthDate(), req.getBirthDate())) member.setBirthDate(req.getBirthDate());
		if(!Objects.equals(member.getAddress(), req.getAddress())) member.setAddress(req.getAddress());
		// 5. 存回去
		MEMBER_REPO.save(member);
	}
	
	public MemberVO findByMemberId() {
		return null;
	}
	
	public MemberVO findByEmail() {
		return null;
	}
	
	// ........
}
