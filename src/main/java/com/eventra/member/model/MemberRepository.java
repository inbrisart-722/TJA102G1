package com.eventra.member.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberVO, Integer>{

	Optional<MemberVO> findByEmail(String email);

	Optional<MemberVO> findByGoogleId(String googleId);
	
	Optional<MemberVO> findByGithubId(String githubId);
	
	Optional<MemberVO> findByFacebookId(String facebookId);
}
