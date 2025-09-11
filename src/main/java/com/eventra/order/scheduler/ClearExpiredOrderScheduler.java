package com.eventra.order.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventra.order.model.OrderService;

@Component
public class ClearExpiredOrderScheduler {
	
	private final OrderService ORDER_SERVICE;

	public ClearExpiredOrderScheduler(OrderService orderService) {
		this.ORDER_SERVICE = orderService;
	}
	
//	@Scheduled(fixedRate = 60_000)
	@Scheduled(cron = "1 * * * * ?") // 秒 分 時 日 月 星期
	public void clearExpirdeOrders() {
//		ORDER_SERVICE.clearExpiredOrders();
//		System.out.println("1 clearExpiredOrderScheduler calling...");
	}
}
