package com.eventra.platform_announcement.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlatformAnnouncementService {

	@Autowired
	PlatformAnnouncementRepository repository;
	
	// 儲存/更新
	public void saveAnn(PlatformAnnouncementVO annVO) {
	    // 若有id, UpdatedAt也要一起更新
	    if (annVO.getPlatformAnnouncementId() != null) {
	        annVO.setUpdatedAt(Timestamp.from(Instant.now()));
	    }
	    repository.save(annVO);
	}
	
	// 刪除
	public void deleteByAnnId(Integer platformAnnouncementId) {
		repository.deleteByAnnId(platformAnnouncementId);
	}
	
	// 修改用, 取單筆資料
	public PlatformAnnouncementVO getOneAnn(Integer platformAnnouncementId) {
		Optional<PlatformAnnouncementVO> optional = repository.findById(platformAnnouncementId);
		return optional.orElse(null);
	}
	
	// 分頁
    public Page<PlatformAnnouncementVO> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    // 模糊查詢
    public Page<PlatformAnnouncementVO> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        return repository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
    }

}
