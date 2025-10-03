package com.eventra.exhibitor.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitorRepository extends JpaRepository<ExhibitorVO, Integer> {
	
	Optional<ExhibitorVO> findByBusinessIdNumber(String businessIdNumber);
	
	boolean existsByBusinessIdNumber(String businessIdNumber);
	boolean existsByEmail(String email);
}
