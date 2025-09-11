package com.eventra.favorite.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.eventra.favorite.dto.FavoriteToggleDTO;
import com.eventra.favorite.model.FavoriteService;
import com.eventra.favorite.model.FavoriteVO;

@Controller
@RequestMapping("/favorite")
public class FavoriteController {

	@Autowired
	private FavoriteService favSvc;


    @GetMapping
    public String favorite() {
    	return "front-end/admin"; // 對應 templates/front-end/admin.html
    }
    
    // 呼叫controller時 帶入該會員的收藏list
    @ModelAttribute("favList")
    protected List<FavoriteVO> findFavoritesByMember(){
    	List<FavoriteVO> list = favSvc.findFavoritesByMember(1); // 用會員1測試
		return list;
    }
	
	// ========== 切換收藏(ajax) ==========
	@PostMapping("/toggle")
	@ResponseBody
	public FavoriteToggleDTO toggleFavorite(
			@RequestParam("memId") Integer memId,
			@RequestParam("exhId") Integer exhId) {
		try {
			boolean favoriteStatus = favSvc.toggleFavorite(memId, exhId);
			FavoriteToggleDTO dto = new FavoriteToggleDTO(exhId, favoriteStatus, true);
			return dto;
		} catch (Exception e) {
			return new FavoriteToggleDTO(exhId, false, false);
		}

	}

// ========== 檢查會員是否已收藏某展覽, 寫收藏狀態為1(ajax) ==========
	@GetMapping("/check")
	@ResponseBody
	public FavoriteToggleDTO checkFavorite(
	        @RequestParam("memId") Integer memId,
	        @RequestParam("exhId") Integer exhId) {
	    try {
	    	// 先查詢是否有該筆收藏
	    	Optional<FavoriteVO> opt = favSvc.findByMemberIdAndExhibitionId(memId, exhId);
	    	
	    	if (opt.isPresent()) {
	            FavoriteVO fav = opt.get();
	            boolean isFavorited = (fav.getFavoriteStatus() == 1);
	            return new FavoriteToggleDTO(exhId, isFavorited, true);
	        } else {
	        	return new FavoriteToggleDTO(exhId, false, true);
	        }
	    } catch (Exception e) {
	        return new FavoriteToggleDTO(exhId, false, false);
	    }
	}

}
