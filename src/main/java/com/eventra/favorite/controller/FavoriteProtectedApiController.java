package com.eventra.favorite.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.favorite.dto.FavoriteDTO;
import com.eventra.favorite.model.FavoriteService;
import com.eventra.favorite.model.FavoriteVO;

@RestController
@RequestMapping("/api/front-end/protected/favorite/")
public class FavoriteProtectedApiController {
	
	@Autowired
	private FavoriteService favSvc;
	
	@Autowired
	private ExhibitionRepository exhRepo; 	// 查詢展覽名稱用
	
	// ========== 切換收藏 ==========
	@PostMapping("/toggle")
	public FavoriteDTO toggleFavorite(@RequestParam("exhId") Integer exhId) {
		try {
			// 1. 切換收藏狀態 (有則取消, 沒有則新增)
			Integer memId = Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
	        boolean favoriteStatus = favSvc.toggleFavorite(memId, exhId);

            // 2. 回傳新的收藏狀態 (DTO 給前端 AJAX 用)
            return new FavoriteDTO(exhId, null, favoriteStatus, null, null);
        } catch (Exception e) {
            return new FavoriteDTO(exhId, null, false, null, null);
        }
    }

	
	// ========== 載入會員所有收藏清單(status = 1) ==========
		@GetMapping("/list")
		public List<FavoriteDTO> listFavorites() {
			// 1. 從 SecurityContextHolder 拿登入id
		    Integer memId = Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
		    
			// 2. 取會員收藏清單 (只撈 favorite_status = 1的資料)
		    List<FavoriteVO> voList = favSvc.findFavoritesByMember(memId);
			
			// 3. 建立 List 容器, 用來存放轉換好的 FavoriteDTO (第1層 集合)
		    List<FavoriteDTO> dtoList = new ArrayList<>();
		    
			// 4. 用 for 迴圈把每筆 VO 轉成 DTO
		    for (FavoriteVO fav : voList) {
		        Optional<ExhibitionVO> opt = exhRepo.findById(fav.getExhibitionId());
		        if (opt.isPresent()) {
		            ExhibitionVO exh = opt.get();
		            String name = exh.getExhibitionName();
		            boolean status = (fav.getFavoriteStatus() != null && fav.getFavoriteStatus() == 1);

		            dtoList.add(new FavoriteDTO(
		                fav.getExhibitionId(),
		                name,
		                status,
		                exh.getAverageRatingScore(),   // Formula 已經算好
		                exh.getTotalRatingCount()      // 評價總數
		            ));
		        }
		    }
		    return dtoList;
		}

		// ========== 檢查會員是否已收藏展覽(status = 1) ==========
		@GetMapping("/check")
		public FavoriteDTO checkFavorite(@RequestParam("exhId") Integer exhId) {
			try {
				// 1. 從 SecurityContextHolder 拿登入id
		        Integer memId = Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
		        
		        // 2. 查詢收藏, 會員是否收藏展覽
		        Optional<FavoriteVO> opt = favSvc.findByMemberIdAndExhibitionId(memId, exhId);
		        
				// 3. 如果有資料:
				if (opt.isPresent()) {
					FavoriteVO fav = opt.get();
					// 收藏狀態 = 1, 有收藏
					boolean isFavorited = (fav.getFavoriteStatus() != null && fav.getFavoriteStatus() == 1);
	                return new FavoriteDTO(exhId, null, isFavorited, null, null);
				} else {
					// 收藏狀態 = 0 / null, 沒收藏
					return new FavoriteDTO(exhId, null, false, null, null);
				}
			} catch (Exception e) {
				return new FavoriteDTO(exhId, null, false, null, null);
			}
		}
}
