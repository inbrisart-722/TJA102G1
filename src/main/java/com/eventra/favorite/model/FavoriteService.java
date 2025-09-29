package com.eventra.favorite.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class FavoriteService {

	@Autowired
	FavoriteRepository repository;
	
	// 查詢會員所有收藏 (status = 1)
	public List<FavoriteVO> findFavoritesByMember(Integer memId) {
		return repository.findFavoritesByMemberWithExhibition(memId);
    }
	
	// 修改收藏狀態
	public int updateStatus(Integer favStatus, Integer favId) {
        return repository.updateStatus(favStatus, favId);
    }
    
    // 查詢會員是否已有收藏展覽 (UK: memberId + exhibitionId)
	public Optional<FavoriteVO> findByMemberIdAndExhibitionId(Integer memId, Integer exhId) {
        return repository.findByMemberIdAndExhibitionId(memId, exhId);
    }
	
    // 收藏按鈕切換用
	@Transactional
	public boolean toggleFavorite(Integer memId, Integer exhId) {
	    Optional<FavoriteVO> opt = repository.findByMemberIdAndExhibitionId(memId, exhId);

	    if (opt.isPresent()) {
	        FavoriteVO favVO = opt.get();
	        Integer favId = favVO.getFavoriteId();
	        Integer newStatus = (favVO.getFavoriteStatus() != null && favVO.getFavoriteStatus() == 1) ? 0 : 1;

	        repository.updateStatus(newStatus, favId);
	        return newStatus == 1;
	    } else {
	        FavoriteVO favVO = new FavoriteVO();
	        favVO.setMemberId(memId);
	        favVO.setExhibitionId(exhId);
	        favVO.setFavoriteStatus(1);
	        repository.save(favVO);
	        return true;
	    }
    }
	
	
}

