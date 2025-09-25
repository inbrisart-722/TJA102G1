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
	
	
//	/* 更新展覽時觸發通知用, 編輯展覽會呼叫此方法 */
//	ExhibitionVO updateExhibition(Integer exhibitionId, ExhibitionCreateDTO dto);

	
}
