package com.eventra.rating.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.member.model.MemberVO;
import com.eventra.order.model.OrderStatus;
import com.eventra.order_item.model.OrderItemRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional(readOnly = true)
public class RatingService {

	@PersistenceContext
	private EntityManager entityManager;
	
	private final RatingRepository RATING_REPO;
	private final OrderItemRepository ORDER_ITEM_REPO;
	private final ExhibitionRepository EXHIBITION_REPO;
	
	public RatingService(RatingRepository ratingRepository, OrderItemRepository orderItemRepository, ExhibitionRepository exhibitionRepository) {
		this.RATING_REPO = ratingRepository;
		this.ORDER_ITEM_REPO = orderItemRepository;
		this.EXHIBITION_REPO = exhibitionRepository;
	}

	public GetMyRatingResDTO getRating(Integer exhibitionId, Integer memberId) {
		// resDTO 參數 1
		boolean canRate = ORDER_ITEM_REPO.existsByOrder_Member_MemberIdAndExhibitionTicketType_ExhibitionIdAndOrder_OrderStatus(memberId, exhibitionId, OrderStatus.已付款);
		System.out.println(canRate);
		// resDTO 參數 2
		Byte originalRating = null;
		if(canRate) originalRating = RATING_REPO.getRatingScore(exhibitionId, memberId);
		
		System.out.println(originalRating);
			
		GetMyRatingResDTO res = new GetMyRatingResDTO().setStatus("success").setCanRate(canRate).setOriginalRating(originalRating);
		return res;
	}
	
	@Transactional
	public UpsertRatingResDTO upsertRating(Integer exhibitionId, Integer memberId, Byte ratingScore) {
		UpsertRatingResDTO res = new UpsertRatingResDTO();
		// status, canRate, originalRating;
		boolean canRate = ORDER_ITEM_REPO.existsByOrder_Member_MemberIdAndExhibitionTicketType_ExhibitionIdAndOrder_OrderStatus(memberId, exhibitionId, OrderStatus.已付款);
		// 再次確認可評分
		if(canRate == true) {
			System.out.println("can rate");
			RatingVO ratingVOOriginal = RATING_REPO.getRating(exhibitionId, memberId);
			// 1 過去沒有評分過 => 直接 save
			if(ratingVOOriginal == null) {
				System.out.println("haven't rated");
				RatingVO ratingVONew = new RatingVO.Builder()
						.exhibition(entityManager.getReference(ExhibitionVO.class, exhibitionId))
						.member(entityManager.getReference(MemberVO.class, memberId))
						.ratingScore(ratingScore)
						.build();
				RATING_REPO.save(ratingVONew);
				
				EXHIBITION_REPO.updateTotalRatingCount(exhibitionId, 1);
				EXHIBITION_REPO.updateTotalRatingScore(exhibitionId, ratingScore);
//				entityManager.flush();
			}
			// 2 過去有評分過 => 直接 update
			else if(ratingVOOriginal != null) {
				Byte ratingScoreOriginal = ratingVOOriginal.getRatingScore();
				
				ratingVOOriginal.setRatingScore(ratingScore);
				
				EXHIBITION_REPO.updateTotalRatingScore(exhibitionId, (byte)(ratingScore - ratingScoreOriginal) );
//				entityManager.flush();
			}
			// 3 更新展覽評分 
			
			ExhibitionVO exhibitionVO = EXHIBITION_REPO.findById(exhibitionId).orElseThrow();
			Integer totalRatingCount = exhibitionVO.getTotalRatingCount();
			Double averageRatingScore = exhibitionVO.getAverageRatingScore(); // not totalRatingScore
			
			res.setStatus("success").setTotalRatingCount(totalRatingCount).setAverageRatingScore(averageRatingScore);
		}
		// 根本不能評分，被前端騙了（使用者篡改等狀況） => return .setStatus("failed")
		else if(canRate == false) {
			res.setStatus("failed: cannot rate");
		}
		// status, ratingCount, averageRatingScore,  
		return res;
	}
}
