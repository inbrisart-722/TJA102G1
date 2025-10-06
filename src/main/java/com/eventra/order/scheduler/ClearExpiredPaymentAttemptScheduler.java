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
	
	// !!! -> 超過 40 mins but 訂單 < 120mins -> 同步調整訂單狀態為 付款失敗 讓用戶可以再次送
//	@Scheduled(fixedRate = 60_000)
	@Scheduled(cron = "0 * * * * ?") // 秒 分 時 日 月 星期
	public void clearExpiredPaymentAttempts() {
		System.out.println("-----0: 先掃 40分鐘以上但仍為 pending 的 payment attempt-----");
		ORDER_SERVICE.clearExpiredPaymentAttempts();
	}
}
// clearExpiredPaymentAttempts 把 payment attempt 掃掉之前，會先打 api

//ReturnURL 是「交易結論」的通知，不是「每次進金流」的通知。
//如果交易沒有結論（例如用戶自己中斷、驗證失敗沒授權成功），那時候不會立即給 ReturnURL。
//所以你系統要做兩件事：
//1.
//ReturnURL → 當成最權威依據，有來就一定處理。
//ClientBackURL/OrderResultURL → 也要接，因為有些中斷/驗證失敗的狀況 ReturnURL 不會來。
//2.
//定期查詢 API (QueryTradeInfo)，補強「漏單」的情境（例如用戶跳出沒付款，訂單就掛著）。

//綠界官方描述的使用情境
//-- 查詢訂單使用情境說明: https://developers.ecpay.com.tw/?p=2887
//1. 當取得付款結果通知時，呼叫查詢訂單API驗證付款結果
//2. 當呼叫產生訂單API後，40分鐘未收到任何通知時，呼叫查詢訂單API取得付款結果
//-- 查詢訂單 api: https://developers.ecpay.com.tw/?p=2890