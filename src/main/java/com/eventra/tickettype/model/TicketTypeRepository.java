package com.eventra.tickettype.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketTypeVO, Integer> {
	
	Optional<TicketTypeVO> findByTicketTypeName(String ticketTypeName);
}
