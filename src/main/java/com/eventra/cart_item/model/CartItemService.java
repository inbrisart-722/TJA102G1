package com.eventra.cart_item.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibitiontickettype.model.ExhibitionTicketTypeRepository;
import com.eventra.exhibitiontickettype.model.ExhibitionTicketTypeVO;
import com.util.MillisToMinutesSecondsUtil;

@Service
@Transactional // read-only 有可能呼叫 modifying 這隻就直接標一起
public class CartItemService {
	
	private static final long CART_EXPIRY_MILLIS = 30 * 60 * 1000L;
	
	private final CartItemRedisRepository CART_ITEM_REDIS_REPO;
	private final ExhibitionRepository EXHIBITION_REPO;
	private final ExhibitionTicketTypeRepository EXHIBITION_TICKET_TYPE_REPO;
	
	public CartItemService(CartItemRedisRepository cartItemRedisRepository, ExhibitionRepository exhibitionRepository, ExhibitionTicketTypeRepository exhibitionTicketTypeRepository) {
		this.CART_ITEM_REDIS_REPO = cartItemRedisRepository;
		this.EXHIBITION_REPO = exhibitionRepository;
		this.EXHIBITION_TICKET_TYPE_REPO = exhibitionTicketTypeRepository;
	}
	
	private void cleanupExpired(Integer memberId, Long now) {
		/* ********* 1st part : 清理過期購物車 ********* */
		List<CartItemRedisVO> listOfVOs = CART_ITEM_REDIS_REPO.cleanupExpired(memberId, now);
		if(listOfVOs == null || listOfVOs.isEmpty()) return;
		/* ********* 2nd part : 加回庫存 ********* */ // AI 提示要利用 redis 原子性不要直接操作 MySQL，Phase II
		Map<Integer, Integer> qtyByExhId = listOfVOs.stream()
				.collect(Collectors.groupingBy(CartItemRedisVO :: getExhibitionId
						, Collectors.summingInt(CartItemRedisVO :: getQuantity)));
		
		for(Map.Entry<Integer, Integer> entry : qtyByExhId.entrySet())
			EXHIBITION_REPO.updateSoldTicketQuantity(entry.getKey(), -entry.getValue());		
	}
	
	public void addCartItem(AddCartItemReqDTO req, Integer memberId) {
		cleanupExpired(memberId, System.currentTimeMillis());
		// AddCartItemRequDTO -> exhibitionId, ticketDatas
		Integer exhibitionId = req.getExhibitionId();
		Map<String, Integer> ticketDatas = req.getTicketDatas();
		
		for(Map.Entry<String, Integer> entry : ticketDatas.entrySet()) {
			// 拼好 CartItemRedisVO ---> CartItemRedisRepository 去操作
			Integer quantity = entry.getValue();
			ExhibitionTicketTypeVO ettVO = EXHIBITION_TICKET_TYPE_REPO.findByExhibitionIdAndTicketTypeId(exhibitionId, Integer.valueOf(entry.getKey())).orElseThrow();
			/* ********* 1st part : 增加展覽售票量 ********* */
			EXHIBITION_REPO.updateSoldTicketQuantity(exhibitionId, quantity);
			/* ********* 2nd part : 新增會員購物車明細 ********* */
			CartItemRedisVO cartItemRedisVO = new CartItemRedisVO.Builder()
					.cartItemId(CART_ITEM_REDIS_REPO.getCartItemId())
					.memberId(memberId)
					.exhibitionTicketTypeId(ettVO.getExhibitionTicketTypeId())
					.exhibitionId(exhibitionId)
					.exhibitionName(ettVO.getExhibition().getExhibitionName())
					.ticketTypeName(ettVO.getTicketType().getTicketTypeName())
					.quantity(quantity)
					.price(ettVO.getPrice())
					.createdAt(System.currentTimeMillis())
					.build();
			
			CART_ITEM_REDIS_REPO.addCartItem(cartItemRedisVO);
		}
		// 後端回應 ... 還有沒有票！！！（這邊很重要）updateSoldTicketQuantity 就可能失敗
	}
	
	public String removeOneCartItem(Integer cartItemId, Integer memberId) {
		/* ********* 1st part : 移除會員購物車明細 並 取得 被刪除VO ********* */
		CartItemRedisVO vo = CART_ITEM_REDIS_REPO.removeOneCartItem(cartItemId, memberId);
		if(vo == null) return "failed";
;
		/* ********* 2nd part : 用 被刪除VO 找到 需扣除售票數之 展覽id ********* */
		List<Integer> ids = EXHIBITION_TICKET_TYPE_REPO
				.findExhibitionId( Collections.singleton(vo.getExhibitionTicketTypeId()) );
		
		Integer exhibitionId = ids.isEmpty() ? null : ids.get(0);
		/* ********* 3rd part : 扣除展覽售票量 ********* */
		Integer quantity = vo.getQuantity();
		EXHIBITION_REPO.updateSoldTicketQuantity(exhibitionId, -quantity);
		return "success";
	}
	
	public String removeAllCartItem(Integer memberId) {
		/* ********* 1st part : 移除會員所有購物車明細 並 取得 所有被刪除VO(list) ********* */
		List<CartItemRedisVO> listOfVOs = CART_ITEM_REDIS_REPO.removeAllCartItem(memberId);
		
		if(listOfVOs == null || listOfVOs.isEmpty()) return "failed";
		
		Map<Integer, Integer> qtyByExhId = listOfVOs.stream()
				.collect(Collectors.groupingBy(CartItemRedisVO :: getExhibitionId
						, Collectors.summingInt(CartItemRedisVO :: getQuantity)));
		
		for(Map.Entry<Integer, Integer> entry : qtyByExhId.entrySet())
			EXHIBITION_REPO.updateSoldTicketQuantity(entry.getKey(), -entry.getValue());
		
		return "success";
		
		// 以下為 購物車明細 不帶 qty 之 archive 版本
//		List<Integer> listOfETTIds = listOfVOs.stream()
//				.map(CartItemRedisVO :: getExhibitionTicketTypeId)
//				.collect(Collectors.toList());
		/* ********* 2nd part : 用 所有被刪除VO(list) 找到 所有需扣除售票數之 展覽id(list) ********* */
//		List<Integer> listOfIds = EXHIBITION_TICKET_TYPE_REPO
//				.findExhibitionidsByExhibitionTicketTypeIdIn(listOfETTIds);
		/* ********* 3rd part : 同展覽id 就聚合起來 ********* */
//		Map<Integer, Long> listOfIdsGrouped = listOfIds.stream().collect(Collectors.groupingBy( Function.identity(), Collectors.counting() ) );
		/* ********* 4th part : 扣除 各展覽售票量 ********* */
//		for(Map.Entry<Integer, Long> entry : listOfIdsGrouped.entrySet()) {
//			Integer exhibitionId = entry.getKey();
//			Integer quantity = Math.toIntExact(entry.getValue()); // 超範圍會丟 ArithmeticException
//			
//			EXHIBITION_REPO.updateSoldTicketQuantity(exhibitionId, -quantity);
//		}
	}
	
	public List<GetCartItemResDTO> getAllCartItem(Integer memberId) {
		cleanupExpired(memberId, System.currentTimeMillis());
		/* ********* 3rd part : 扣除展覽售票量 ********* */
		List<CartItemRedisVO> listOfVOs = CART_ITEM_REDIS_REPO.getAllCartItem(memberId);
		if(listOfVOs == null || listOfVOs.isEmpty()) return null;
		
		List<GetCartItemResDTO> listOfResDTOs = new ArrayList<>(); 
		listOfVOs.forEach(el -> {
			String expirationTime = MillisToMinutesSecondsUtil.convert(CART_EXPIRY_MILLIS - (System.currentTimeMillis() - el.getCreatedAt()));
					
			GetCartItemResDTO resDTO = 
					new GetCartItemResDTO.Builder()
					.cartItemId(el.getCartItemId())
					.exhibitionName(el.getExhibitionName())
					.ticketTypeName(el.getTicketTypeName())
					.quantity(el.getQuantity())
					.price(el.getPrice())
					.expirationTime(expirationTime)
					.build();
					
			listOfResDTOs.add(resDTO);
		});
		return listOfResDTOs;
	}
	
	public GetMyExpirationResDTO getEarliestExpiration(Integer memberId) {
		cleanupExpired(memberId, System.currentTimeMillis());
		/* ********* 1st part : 取得最小時間那筆明細 ********* */
		CartItemRedisVO vo = CART_ITEM_REDIS_REPO.getEarliestCartItem(memberId); 
		/* ********* 2nd part : 根本沒取到就 88 ! ********* */
		GetMyExpirationResDTO res = new GetMyExpirationResDTO();
		if(vo == null) res.setStatus("failed");
		/* ********* 3rd part : 30 分鐘去扣（現在時間 - 存入時間) 並且直接轉成前端想要的格式 ********* */
		else {
			String backgroundExpireTime = MillisToMinutesSecondsUtil.convert(CART_EXPIRY_MILLIS - (System.currentTimeMillis() - vo.getCreatedAt()));
			res.setStatus("success").setBackgroundExpireTime(backgroundExpireTime);
		}
		return res;
	}
}
