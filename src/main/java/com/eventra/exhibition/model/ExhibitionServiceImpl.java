package com.eventra.exhibition.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.exhibitor.backend.controller.dto.ExhibitionCreateDTO;
import com.eventra.exhibitor.model.ExhibitorVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ExhibitionServiceImpl implements ExhibitionService {

	private final ExhibitionRepository repository;
	
	@PersistenceContext
	 private EntityManager entityManager;
	

	@Autowired
	public ExhibitionServiceImpl(ExhibitionRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public void addExhibition(ExhibitionCreateDTO dto, Integer exhibitorId) {
		
		// 將 DTO 資料轉換成 Entity
		ExhibitionVO exhibitionVO = new ExhibitionVO();
		exhibitionVO.setExhibitionTicketTypes(dto.getExhibitionTicketTypes());
		exhibitionVO.setExhibitionName(dto.getExhibitionName());
		exhibitionVO.setStartTime(dto.getStartTime());
		exhibitionVO.setEndTime(dto.getEndTime());
		exhibitionVO.setLocation(dto.getLocation());
		exhibitionVO.setTicketStartTime(dto.getTicketStartTime());
		exhibitionVO.setTotalTicketQuantity(dto.getTotalTicketQuantity());
		exhibitionVO.setDescription(dto.getDescription());
		exhibitionVO.setExhibitionStatusId(4);
		
		exhibitionVO.setExhibitorVO(entityManager.getReference(ExhibitorVO.class, exhibitorId)); 
		
		repository.save(exhibitionVO);
		
	}
}
