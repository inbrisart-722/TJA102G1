package com.eventra.cart_item.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventra.cart_item.model.CartItemRedisRepository;
import com.eventra.cart_item.model.CartItemService;

@Component
public class ExpiringCartItemLineBotScheduler {
	
	private final CartItemService CART_ITEM_SERVICE;
	private final CartItemRedisRepository CART_ITEM_REDIS_REPO;
	
	public ExpiringCartItemLineBotScheduler(CartItemService cartItemService, CartItemRedisRepository cartItemRedisRepository) {
		this.CART_ITEM_SERVICE = cartItemService;
		this.CART_ITEM_REDIS_REPO = cartItemRedisRepository;
	}
	
	@Scheduled(fixedRate = 30_000)
	public void checkFiveMinutesLeft() {
		System.out.println("-----每30秒掃: ExpiringCartItemLineBotScheduler: checkFiveMinutesLeft 找到所有4.93分鐘內過期且5分鐘內未通知過的用戶-----");
		CART_ITEM_SERVICE.pushGlobalExpiringCartItem();
	}
	
	@Scheduled(fixedRate = 45_000)
	public void cleanupGlobalExp(){
		System.out.println("-----每45秒掃: ExpiringCartItemLineBotScheduler: cleanupGlobalExp 調整 cart:items:globalExp 裡面的會員過期時間取出最快過期那筆-----");
		CART_ITEM_REDIS_REPO.cleanupGlobalExp();
	}
}
