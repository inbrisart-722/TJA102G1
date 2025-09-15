package com.eventra.member.test.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventra.member.model.MemberVO;

public interface TestMemberRepository extends JpaRepository<TestMemberVO, Integer>{

	Optional<TestMemberVO> findByEmail(String email);
}
