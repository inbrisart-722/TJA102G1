package com.eventra.exhibitor.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventra.member.model.MemberVO;

public interface ExhibitorRepository extends JpaRepository<ExhibitorVO, Integer> {
	
	Optional<ExhibitorVO> findByBusinessIdNumber(String businessIdNumber);
}
