package com.eventra.exhibition.model;

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
	
}
