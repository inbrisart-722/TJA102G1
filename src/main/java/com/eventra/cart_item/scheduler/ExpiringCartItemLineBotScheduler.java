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
		System.out.println("ExpiringCartItemLineBotScheduler: checkFiveMinutesLeft");
		CART_ITEM_SERVICE.pushGlobalExpiringCartItem();
	}
	
	@Scheduled(fixedRate = 45_000)
	public void cleanupGlobalExp(){
		System.out.println("ExpiringCartItemLineBotScheduler: cleanupGlobalExp");
		CART_ITEM_REDIS_REPO.cleanupGlobalExp();
	}
}
