package com.eventra.cart_item.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventra.cart_item.model.CartItemService;

@Component
public class ExpiringCartItemLineBotScheduler {
	
	private final CartItemService CART_ITEM_SERVICE;
	
	public ExpiringCartItemLineBotScheduler(CartItemService cartItemService) {
		this.CART_ITEM_SERVICE = cartItemService;
	}
	
	@Scheduled(fixedRate = 20_000)
	public void checkFiveMinutesLeft() {
//		Long now = System.currentTimeMillis();
//		CART_ITEM_SERVICE.checkFiveMinutesLeft(now);
	}
}
