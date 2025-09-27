package com.eventra.exhibition.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
	/**
	 * 分頁(參數使用int是因為JPA的page方法預設是int所以可以直接丟進去，Integer要額外處理null的判斷)
	 * @param int
	 * 			page
	 * @param int
	 * 			size
	 * @return
	 */
	Page<ExhibitionVO> getExhibitionsPage(int page, int size);
	
	ExhibitionVO findById(Integer id);
	
	void updateExhibition(ExhibitionCreateDTO dto, Integer id);
  
//	/* 更新展覽時觸發通知用, 編輯展覽會呼叫此方法 */
//	ExhibitionVO updateExhibition(Integer exhibitionId, ExhibitionCreateDTO dto);
	
	// === 狀態分頁查詢（給列表頁用） ===
    Page<ExhibitionVO> findAll(Integer exhibitorId, int page, int size);

    Page<ExhibitionVO> findDrafts(Integer exhibitorId, Integer draftStatusId, int page, int size);

    Page<ExhibitionVO> findNotOnSale(Integer exhibitorId, int page, int size);

    Page<ExhibitionVO> findOnSale(Integer exhibitorId, int page, int size);

    Page<ExhibitionVO> findEnded(Integer exhibitorId, int page, int size);
}
