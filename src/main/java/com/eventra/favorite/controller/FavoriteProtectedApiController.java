package com.eventra.favorite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.favorite.dto.FavoriteDTO;
import com.eventra.favorite.model.FavoriteService;

@RestController
@RequestMapping("/api/front-end/protected/favorite/")
public class FavoriteProtectedApiController {
	
	@Autowired
	private FavoriteService favSvc;
	
	// ========== 切換收藏 ==========
	@PostMapping("/toggle")
	public FavoriteDTO toggleFavorite(@RequestParam("exhId") Integer exhId) {
		try {
			// 1. 切換收藏狀態 (有則取消, 沒有則新增)
			Integer memId = Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
	        boolean favoriteStatus = favSvc.toggleFavorite(memId, exhId);

            // 2. 回傳新的收藏狀態 (DTO 給前端 AJAX 用)
            return new FavoriteDTO(exhId, null, favoriteStatus);
        } catch (Exception e) {
            return new FavoriteDTO(exhId, null, false);
        }
    }

}
