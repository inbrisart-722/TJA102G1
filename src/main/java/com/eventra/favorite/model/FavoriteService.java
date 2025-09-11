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
		return repository.findFavoritesByMember(memId);
	}
	
	// 修改收藏狀態
    public int updateStatus(Integer favStatus, Integer favId) {
    	return repository.updateStatus(favStatus, favId);
    }
    
    // 查詢會員是否已有收藏展覽 (UK: memberId + exhibitionId)
    public Optional<FavoriteVO> findByMemberIdAndExhibitionId(Integer memId, Integer exhId){
    	return repository.findByMemberIdAndExhibitionId(memId, exhId);
    }
	
    // 收藏按鈕切換用
    @Transactional
    public boolean toggleFavorite(Integer memId, Integer exhId) {
    	Optional<FavoriteVO> opt = repository.findByMemberIdAndExhibitionId(memId, exhId);
    	
    	// 判斷(opt物件)會員是否有收藏展覽
    	if (opt.isPresent()) { 
    		// true,  favorite_status 1改0 or 0改1
            FavoriteVO favVO = opt.get();
            Integer favId = favVO.getFavoriteId();
            Integer oldStatus = (favVO.getFavoriteStatus() == null) ? 0 : favVO.getFavoriteStatus();
            Integer favStatus = (oldStatus == 1) ? 0 : 1;

            repository.updateStatus(favStatus, favId);
            return favStatus == 1; // 回傳切換結果是否為true
        } else {
        	// false, 建立紀錄
            FavoriteVO favVO = new FavoriteVO();
            favVO.setMemberId(memId);
            favVO.setExhibitionId(exhId);
            repository.save(favVO);
            return true;
        }
    }
	
	
}

