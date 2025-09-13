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
	
	// 間隔 30 秒 給 Query api 使用
	@Scheduled(cron = "30 * * * * ?") // 秒 分 時 日 月 星期
	public void clearExpiredOrders() {
//		System.out.println("-----1: 再掃 120 分鐘以上且沒有 payment attempt 為 pending 的 order-----");
//		ORDER_SERVICE.clearExpiredOrders();
	}
}
