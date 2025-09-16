package com.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.eventra.order.model.ECPayCallbackReqDTO;
import com.properties.ECPayProperties;

@Component
public class ECPayUtils {
	
	private final String HASH_KEY;
	private final String HASH_IV;

	public ECPayUtils(ECPayProperties ECPayProps) {
		this.HASH_KEY = ECPayProps.hashKey();
		this.HASH_IV = ECPayProps.hashIv();
	};
	
//	public Map<String, String> genCheckMap(ECPayQueryResDTO res) {
//	    Map<String, String> fields = new HashMap<>();
//
//	    if (res.getMerchantID() != null) fields.put("MerchantID", res.getMerchantID());
//	    if (res.getMerchantTradeNo() != null) fields.put("MerchantTradeNo", res.getMerchantTradeNo());
//	    if (res.getStoreID() != null) fields.put("StoreID", res.getStoreID());
//	    if (res.getTradeNo() != null) fields.put("TradeNo", res.getTradeNo());
//	    if (res.getTradeAmt() != null) fields.put("TradeAmt", String.valueOf(res.getTradeAmt()));
//	    if (res.getPaymentDate() != null) fields.put("PaymentDate", res.getPaymentDate());
//	    if (res.getPaymentType() != null) fields.put("PaymentType", res.getPaymentType());
//	    if (res.getHandlingCharge() != null) fields.put("HandlingCharge", String.valueOf(res.getHandlingCharge()));
//	    if (res.getPaymentTypeChargeFee() != null) fields.put("PaymentTypeChargeFee", String.valueOf(res.getPaymentTypeChargeFee()));
//	    if (res.getTradeDate() != null) fields.put("TradeDate", res.getTradeDate());
//	    if (res.getTradeStatus() != null) fields.put("TradeStatus", res.getTradeStatus());
//	    if (res.getItemName() != null) fields.put("ItemName", res.getItemName());
//	    if (res.getCustomField1() != null) fields.put("CustomField1", res.getCustomField1());
//	    if (res.getCustomField2() != null) fields.put("CustomField2", res.getCustomField2());
//	    if (res.getCustomField3() != null) fields.put("CustomField3", res.getCustomField3());
//	    if (res.getCustomField4() != null) fields.put("CustomField4", res.getCustomField4());
//	    if (res.getCheckMacValue() != null) fields.put("CheckMacValue", res.getCheckMacValue());
//
//	    return fields;
//	}

	public Map<String, String> genCheckMap(ECPayCallbackReqDTO req){
		Map<String, String> fields = new HashMap<>();
		// Populate the map with non-null values from the DTO
        if (req.getMerchantID() != null) fields.put("MerchantID", req.getMerchantID());
        if (req.getMerchantTradeNo() != null) fields.put("MerchantTradeNo", req.getMerchantTradeNo());
        if (req.getStoreID() != null) fields.put("StoreID", req.getStoreID());
        if (req.getRtnCode() != null) fields.put("RtnCode", req.getRtnCode());
        if (req.getRtnMsg() != null) fields.put("RtnMsg", req.getRtnMsg());
        if (req.getTradeNo() != null) fields.put("TradeNo", req.getTradeNo());
        
        // Convert numbers and to String before putting into the Map
        if (req.getTradeAmt() != null) fields.put("TradeAmt", String.valueOf(req.getTradeAmt()));
        if (req.getPaymentDate() != null) fields.put("PaymentDate", req.getPaymentDate());
        
        if (req.getPaymentType() != null) fields.put("PaymentType", req.getPaymentType());
        if (req.getPaymentTypeChargeFee() != null) fields.put("PaymentTypeChargeFee", String.valueOf(req.getPaymentTypeChargeFee()));
        if (req.getTradeDate() != null) fields.put("TradeDate", req.getTradeDate());
        if (req.getPlatformId() != null) fields.put("PlatformId", req.getPlatformId());
        
        if (req.getSimulatePaid() != null) fields.put("SimulatePaid", String.valueOf(req.getSimulatePaid()));
        
        if (req.getCustomField1() != null) fields.put("CustomField1", req.getCustomField1());
        if (req.getCustomField2() != null) fields.put("CustomField2", req.getCustomField2());
        if (req.getCustomField3() != null) fields.put("CustomField3", req.getCustomField3());
        if (req.getCustomField4() != null) fields.put("CustomField4", req.getCustomField4());
		return fields;
	}
	
	// --- 核心：ECPay SHA256 CheckMacValue ---
	public String genCheckMacValue(Map<String, String> rawParams) throws Exception{
		return genCheckMacValue(rawParams, HASH_KEY, HASH_IV);
	}
	private String genCheckMacValue(Map<String, String> rawParams, String hashKey, String hashIV)
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
	private String ecpayUrlEncode(String s) {
		// Java 的 URLEncoder 會把空白轉成 '+'，這點符合 ECPay 規格
		String encoded = URLEncoder.encode(s, StandardCharsets.UTF_8).toLowerCase()
				// ECPay 指定需要還原的符號
				.replace("%21", "!").replace("%28", "(").replace("%29", ")").replace("%2a", "*").replace("%7e", "~");
		return encoded;
	}

	private String bytesToHexUpper(byte[] bytes) {
		StringBuilder out = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			out.append(String.format("%02X", b));
		}
		return out.toString();
	}

	private String toFormUrlEncoded(Map<String, String> params) {
		return params.entrySet().stream().map(e -> urlEncode(e.getKey()) + "=" + urlEncode(e.getValue()))
				.collect(Collectors.joining("&"));
	}

	private String urlEncode(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

}
