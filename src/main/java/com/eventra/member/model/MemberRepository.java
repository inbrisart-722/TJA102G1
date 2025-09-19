package com.eventra.member.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberVO, Integer>{

	Optional<MemberVO> findByEmail(String email);
  
}
