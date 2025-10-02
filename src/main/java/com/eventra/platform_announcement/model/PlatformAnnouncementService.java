package com.eventra.platform_announcement.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatformAnnouncementService {

	@Autowired
	PlatformAnnouncementRepository repository;
	
	public void saveAnn(PlatformAnnouncementVO annVO) {
		repository.save(annVO);
	}
	
	public void deleteByAnnId(Integer platformAnnouncementId) {
		repository.deleteByAnnId(platformAnnouncementId);
	}
	
	public PlatformAnnouncementVO getOneAnn(Integer platformAnnouncementId) {
		Optional<PlatformAnnouncementVO> optional = repository.findById(platformAnnouncementId);
		return optional.orElse(null);
	}
	
	public List<PlatformAnnouncementVO> getAll(){
		return repository.findAll();
	}
	
}
