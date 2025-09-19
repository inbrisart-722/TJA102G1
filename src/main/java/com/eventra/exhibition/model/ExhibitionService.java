package com.eventra.exhibition.model;

import java.util.List;

import org.springframework.data.domain.Page;

import com.eventra.exhibitor.backend.controller.dto.ExhibitionCreateDTO;

/**
 * 
 */
public interface ExhibitionService {

	/**
	 * 新增展覽
	 * 
	 * @param dto
	 * 			ExhibitionCreateDTO
	 * @param exhibitorId
	 * 			Integer		  
	 */
	void addExhibition(ExhibitionCreateDTO dto, Integer exhibitorId) ;
	
	List<ExhibitionVO> getAllExhibitions();
	
	Page<ExhibitionVO> getExhibitionsPage(int page, int size);
	
}
