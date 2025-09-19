package com.eventra.exhibition.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eventra.exhibitionstatus.model.ExhibitionStatusVO;
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

		// 1) 將 DTO 資料轉換成 Entity(VO)
		ExhibitionVO exhibitionVO = new ExhibitionVO();
		exhibitionVO.setExhibitionTicketTypes(dto.getExhibitionTicketTypes());
		exhibitionVO.setExhibitionName(dto.getExhibitionName());
		exhibitionVO.setStartTime(dto.getStartTime());
		exhibitionVO.setEndTime(dto.getEndTime());
		exhibitionVO.setLocation(dto.getLocation());
		exhibitionVO.setTicketStartTime(dto.getTicketStartTime());
		exhibitionVO.setTotalTicketQuantity(dto.getTotalTicketQuantity());
		exhibitionVO.setDescription(dto.getDescription());
		
	    exhibitionVO.setExhibitionStatus(entityManager.getReference(ExhibitionStatusVO.class, 1));

		exhibitionVO.setExhibitorVO(entityManager.getReference(ExhibitorVO.class, exhibitorId));

		/** 圖片儲存處理 **/
		// 2) 第一次 save : 先存到 DB 取得自增 id
		ExhibitionVO saved = repository.save(exhibitionVO);
		Integer id = saved.getExhibitionId(); // 取得資料夾名稱要用的 id

		// 3) 建立 /static/uploads/exhibitions/{id}/ 目錄
		Path baseDir = Paths.get("/Users/lianliwei/uploads/exhibitions", String.valueOf(id));
		try {
			Files.createDirectories(baseDir);
		} catch (IOException e) {
			throw new RuntimeException("建立圖片目錄失敗: " + baseDir, e);
		}

		// 4) 存portrait
		MultipartFile portrait = dto.getPhotoPortrait();
		if(portrait != null && !portrait.isEmpty()) {
			String filename = "p_" + UUID.randomUUID() + "_" + portrait.getOriginalFilename();
			try {
				portrait.transferTo(baseDir.resolve(filename));
				saved.setPhotoPortrait("uploads/exhibitions/" + id + "/" + filename);
			}catch(IOException e) {
				throw new RuntimeException("存 portrait 失敗", e);
			}
		}
		
		// 5) 存 landscape
		MultipartFile landscape = dto.getPhotoLandscape();
		if(landscape != null && !landscape.isEmpty()) {
			String filename = "l_" + UUID.randomUUID() + "_" + landscape.getOriginalFilename();
			try {
				landscape.transferTo(baseDir.resolve(filename));
				saved.setPhotoLandscape("uploads/exhibition/" + id + "/" + filename);
			}catch(IOException e) {
				throw new RuntimeException("存 landscape 失敗", e);
			}
		}

		// 6) 第二次 save : 更新圖片路徑
		repository.save(saved);

	}
	
	// 實作展覽查詢方法
	public List<ExhibitionVO> getAllExhibitions(){
			return repository.findAll();
	}
		
	// 分頁查詢
	public Page<ExhibitionVO> getExhibitionsPage(int page, int size){
		Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
		return repository.findAll(pageable);
	}

	
}
