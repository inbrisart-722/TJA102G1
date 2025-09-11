package com.eventra.order.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventra.order.model.OrderService;

@Component
public class ClearExpiredPaymentAttemptScheduler {
	
	private final OrderService ORDER_SERVICE; 
	
	public ClearExpiredPaymentAttemptScheduler(OrderService orderService) {
		this.ORDER_SERVICE = orderService;
	}
	
//	@Scheduled(fixedRate = 60_000)
	@Scheduled(cron = "0 * * * * ?") // 秒 分 時 日 月 星期
	public void clearExpiredPaymentAttempts() {
//		ORDER_SERVICE.clearExpiredPaymentAttempts();
//		System.out.println("0 clearExpiredPaymentAttemptScheduler calling...");
	}
}
