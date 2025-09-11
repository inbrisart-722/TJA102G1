package com.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class CalcCheckMacValue {
//	HashKey: 5294y06JbISpM5x9
//	HashIV: v77hoKGq4kWxNNIS
	private static final String HASH_KEY = "5294y06JbISpM5x9"; // 測試範例：請換成你的
	private static final String HASH_IV = "v77hoKGq4kWxNNIS"; // 測試範例：請換成你的
	private static final String MERCHANT_ID = "2000132"; // 測試範例：請換成你的

	// --- 核心：ECPay SHA256 CheckMacValue ---
	public static String genCheckMacValue(Map<String, String> rawParams) throws Exception{
		return genCheckMacValue(rawParams, HASH_KEY, HASH_IV);
	}
	private static String genCheckMacValue(Map<String, String> rawParams, String hashKey, String hashIV)
			throws Exception {
		// (A) 取出參數（排除 CheckMacValue），依「參數名稱」升冪排序
		Map<String, String> sorted = rawParams.entrySet().stream()
				.filter(e -> !"CheckMacValue".equalsIgnoreCase(e.getKey()))
				.sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

		// (B) 串接字串：HashKey=...&key=value&...&HashIV=...
		StringBuilder sb = new StringBuilder();
		sb.append("HashKey=").append(hashKey);
		for (Map.Entry<String, String> e : sorted.entrySet()) {
			sb.append('&').append(e.getKey()).append('=').append(e.getValue());
		}
		sb.append("&HashIV=").append(hashIV);

		// (C) 先做 URL encode（ECPay 規則），轉小寫，再做部分還原
		String encoded = ecpayUrlEncode(sb.toString());

		// (D) SHA-256 後轉大寫十六進位
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] digest = md.digest(encoded.getBytes(StandardCharsets.UTF_8));
		return bytesToHexUpper(digest);
	}

	// ECPay 規則：URLEncoder + toLowerCase + 特定字元還原
	private static String ecpayUrlEncode(String s) {
		// Java 的 URLEncoder 會把空白轉成 '+'，這點符合 ECPay 規格
		String encoded = URLEncoder.encode(s, StandardCharsets.UTF_8).toLowerCase()
				// ECPay 指定需要還原的符號
				.replace("%21", "!").replace("%28", "(").replace("%29", ")").replace("%2a", "*").replace("%7e", "~");
		return encoded;
	}

	private static String bytesToHexUpper(byte[] bytes) {
		StringBuilder out = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			out.append(String.format("%02X", b));
		}
		return out.toString();
	}

	private static String toFormUrlEncoded(Map<String, String> params) {
		return params.entrySet().stream().map(e -> urlEncode(e.getKey()) + "=" + urlEncode(e.getValue()))
				.collect(Collectors.joining("&"));
	}

	private static String urlEncode(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

	public static void main(String[] args) throws Exception {
//		// 1) 準備參數（不含 CheckMacValue）
//		Map<String, String> params = new HashMap<>();
//		params.put("MerchantID", MERCHANT_ID);
//		params.put("MerchantTradeNo", "000" + System.currentTimeMillis());
//		params.put("MerchantTradeDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
//		params.put("PaymentType", "aio");
//		params.put("TotalAmount", "1000");
//		params.put("TradeDesc", "Test transaction");
//		params.put("ItemName", "Item1#Item2");
//		params.put("ReturnURL", "https://www.yourdomain.com/receive");
//		params.put("ChoosePayment", "Credit");
//		params.put("EncryptType", "1");
//		
//		// 2) 產生 CheckMacValue
//		String checkMac = genCheckMacValue(params, HASH_KEY, HASH_IV);
//		params.put("CheckMacValue", checkMac);
//		System.out.println("CheckMacValue = " + checkMac);
//
//		// 3) 轉成 x-www-form-urlencoded
//		String formBody = toFormUrlEncoded(params);
//
//		// 4) 送出 HttpClient POST（測試環境）
//		String url = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";
//		HttpClient client = HttpClient.newHttpClient();
//		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
//				.header("Content-Type", "application/x-www-form-urlencoded")
//				.POST(HttpRequest.BodyPublishers.ofString(formBody)).build();
//
//		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//		System.out.println("HTTP Status: " + response.statusCode());
//		System.out.println("Body:\n" + response.body());
		// 通常會回一段HTML表單/頁面；實務上你會把它回給前端或直接 302/auto-submit。

	}
}
