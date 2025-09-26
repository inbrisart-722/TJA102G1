package com.util;

import java.util.concurrent.ThreadLocalRandom;

public class ProviderOrderId36Generator {
	public static String generateMerchantTradeNo() {
		// 1. 取得當前時間毫秒，轉換成 Base36 (0+9 + a~z)
		String timeBase36 = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
		// 2. 產生 4 位隨機英數字
		// %->格式化規則 ; 0->0補滿 ; 4->寬度至少4 ; X -> Hexadecimal
		// ThreadLocalRandom -> Java 亂數產生器（類似 Random），但更適合 multi-thread 情況 -> 每個 thread
		// 有自己的 seed
		String rand4 = String.format("%04X", ThreadLocalRandom.current().nextInt(0, 16 * 16 * 16 * 16));

		// 3. 拼接 13 碼
		return "EC" + timeBase36 + rand4;
	}

	public static String generateOrderId() {
		// 1. 取得當前時間毫秒，轉換成 Base36 (0+9 + a~z)
		String timeBase36 = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
		// 2. 產生 4 位隨機英數字
		// %->格式化規則 ; 0->0補滿 ; 4->寬度至少4 ; X -> Hexadecimal
		// ThreadLocalRandom -> Java 亂數產生器（類似 Random），但更適合 multi-thread 情況 -> 每個 thread
		// 有自己的 seed
		String rand4 = String.format("%04X", ThreadLocalRandom.current().nextInt(0, 16 * 16 * 16 * 16));

		// 3. 拼接 13 碼
		return "LP" + timeBase36 + rand4;
	}
}
