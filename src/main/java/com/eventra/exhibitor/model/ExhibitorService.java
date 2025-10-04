package com.eventra.exhibitor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.exhibitor_review_log.model.ExhibitorReviewLogRedisRepository;
import com.eventra.exhibitorstatus.model.ExhibitorStatusRepository;
import com.eventra.exhibitorstatus.model.ExhibitorStatusVO;

@Service
@Transactional
public class ExhibitorService {

	@Autowired
	private ExhibitorRepository exhibitorRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ExhibitorStatusRepository exhibitorStatusRepo;
	@Autowired
	private ExhibitorReviewLogRedisRepository logRepo;

    public ExhibitorVO getExhibitorById(Integer id) {
        return exhibitorRepo.findById(id).orElse(null);
    }
    
    public boolean existsByBusinessIdNumber(String businessIdNumber) {
    	return exhibitorRepo.existsByBusinessIdNumber(businessIdNumber);
    }
    
    public boolean existsByEmail(String email) {
    	return exhibitorRepo.existsByEmail(email);
    }
    
    public ExhibitorVO findByToken(String token) {
    	Integer exhibitorId = logRepo.getExhibitorIdFromToken(token);
    	if(exhibitorId == null) return null;
    	else return exhibitorRepo.findById(logRepo.getExhibitorIdFromToken(token)).orElse(null);
    }
    
    public void exhibitorRegisterWithToken(ExhibitorRegisterForm form, String token) {
    	Integer exhibitorId = logRepo.getExhibitorIdFromToken(token);
    	
    	ExhibitorVO exhibitor = exhibitorRepo.findById(exhibitorId).orElseThrow();
    	
    	// 重新變回待審核狀態
    	exhibitor.setReviewStatusId(1);
    	// 其他欄位全部都拿這次的註冊資料重新更新過
    	exhibitor.setBusinessIdNumber(form.getBusinessIdNumber());
    	exhibitor.setPasswordHash(passwordEncoder.encode(form.getPassword()));
    	exhibitor.setCompanyName(form.getCompanyName());
    	exhibitor.setCompanyAddress(form.getCompanyAddress());
    	exhibitor.setContactPhone(form.getContactPhone());
    	exhibitor.setEmail(form.getEmail());
    	exhibitor.setBankCode(form.getBankCode());
    	exhibitor.setBankAccountName(form.getBankAccountName());
    	exhibitor.setBankAccountNumber(form.getBankAccountNumber());
    	
    	logRepo.deleteFailureToken(token);
    }
    
    public void exhibitorRegister(ExhibitorRegisterForm form) {
    	ExhibitorVO exhibitor = new ExhibitorVO();
    	
    	exhibitor.setBusinessIdNumber(form.getBusinessIdNumber());
    	exhibitor.setPasswordHash(passwordEncoder.encode(form.getPassword()));
    	exhibitor.setCompanyName(form.getCompanyName());
    	exhibitor.setCompanyAddress(form.getCompanyAddress());
    	exhibitor.setContactPhone(form.getContactPhone());
    	exhibitor.setEmail(form.getEmail());
    	exhibitor.setBankCode(form.getBankCode());
    	exhibitor.setBankAccountName(form.getBankAccountName());
    	exhibitor.setBankAccountNumber(form.getBankAccountNumber());
    	
    	exhibitorRepo.save(exhibitor);
    }
    
    public Page<ExhibitorReviewListPageDTO> findExhibitorsForReview(int page){
    	Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "createdAt"));
    	// 1 待核准 pending
    	// 2 已核准 approved
    	// 3 未通過 failed
    	Page<ExhibitorVO> exhibitors = exhibitorRepo.findByReviewStatusIdIn(Set.of(1, 2, 3), pageable);
    	
    	// 1. 組 dto
    	List<ExhibitorReviewListPageDTO> dtos = new ArrayList<>();
    	for(ExhibitorVO vo : exhibitors.getContent()) {
    		ExhibitorReviewListPageDTO dto = new ExhibitorReviewListPageDTO();
    		
    		dto.setBusinessIdNumber(vo.getBusinessIdNumber());
    		dto.setCompanyName(vo.getCompanyName());
    		dto.setContactPhone(vo.getContactPhone());
    		dto.setEmail(vo.getEmail());
    		dto.setExhibitorId(vo.getExhibitorId());
    		Optional<ExhibitorStatusVO> reviewStatusOP = exhibitorStatusRepo.findById(vo.getReviewStatusId());
    		String reviewStatus = null;
    		if(reviewStatusOP.isPresent()) reviewStatus = reviewStatusOP.get().getReviewStatus(); 
    		dto.setExhibitorReviewStatus(reviewStatus);
    		
    		dtos.add(dto);
    	}
    	
    	return new PageImpl(dtos, exhibitors.getPageable(), exhibitors.getTotalElements());
    }
  	// 平台公告首頁用, 展商總數
	public long countAll() {
		return exhibitorRepo.count();
	}

	// 平台公告首頁用, 待核准展商數量（review_status_id = 1）
	public long countByStatusId(int statusId) {
		return exhibitorRepo.countByReviewStatusId(statusId);
	}
}
