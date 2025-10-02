package com.eventra.rating.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.rating.model.GetMyRatingResDTO;
import com.eventra.rating.model.RatingService;
import com.eventra.rating.model.UpsertRatingResDTO;

@RestController
@RequestMapping("/api/front-end")
public class RatingRestController {

	private final RatingService RATING_SERVICE;
//	private static final Integer TEST_MEMBER = 3;
	// 3 可評價無記錄 4 可評價無紀錄 5 訂單已退款不可評價
	
	public RatingRestController(RatingService ratingService) {
		this.RATING_SERVICE = ratingService;
	}
	
	@GetMapping("/protected/rating/getMyRating")
	public GetMyRatingResDTO getMyRating(@RequestParam("exhibitionId") Integer exhibitionId, Principal principal){
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		return RATING_SERVICE.getRating(exhibitionId, memberId);
	}
	// 前端給：exhibitionId
	// 後端回：status, canRate, originalRating  => addCommentResDTO
	
	@PutMapping("/protected/rating/upsertRating")
	public UpsertRatingResDTO upsertRating
		(@RequestParam("exhibitionId") Integer exhibitionId,
		 @RequestParam("ratingScore") Byte ratingScore,
		 Principal principal ) {
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		return RATING_SERVICE.upsertRating(exhibitionId, memberId, ratingScore);
	}
	// 前端給：exhibitionId, ratingScore
	// 後端回：status, totalRatingCount(nullable), averageRatingScore(nullable) => UpsertRatingResDTO
	
}
