package com.eventra.exhibitor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExhibitorService {

	@Autowired
	private ExhibitorRepository exhibitorRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public ExhibitorVO getExhibitorById(Integer id) {
		return exhibitorRepo.findById(id).orElse(null);
	}

	public boolean existsByBusinessIdNumber(String businessIdNumber) {
		return exhibitorRepo.existsByBusinessIdNumber(businessIdNumber);
	}

	public boolean existsByEmail(String email) {
		return exhibitorRepo.existsByEmail(email);
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

	// 平台公告首頁用, 展商總數
	public long countAll() {
		return exhibitorRepo.count();
	}

	// 平台公告首頁用, 待核准展商數量（review_status_id = 1）
	public long countByStatusId(int statusId) {
		return exhibitorRepo.countByReviewStatusId(statusId);
	}
}
