package com.util;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

//String url = "http://mydomain.com/api/front-end/orderItem/qr-code-verify?orderItemUlid=01JAX...";
//String qrBase64 = QrCodeUtil.generateQrCodeBase64(url);
//
//// 直接塞進 Flex Bubble
//ObjectNode qr = mapper.createObjectNode();
//qr.put("type", "image");
//qr.put("url", qrBase64);
//qr.put("size", "md");

public class QrCodeUtil {
	
	private static final String QRCODE_URL_PREFIX = "http://localhost:8088/front-end/orderItem/qr-code-verify?ticketCode="; 
	
	public static String generateQrCodeBase64(String ticketCode) {
		String url = QRCODE_URL_PREFIX + ticketCode;
		
		try {
			// 產生 QR Code
			BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 250, 250);

			// 寫成 PNG
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(matrix, "PNG", out);

			// 回傳 Base64 Data URI
			return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());

		} catch (Exception e) {
			throw new RuntimeException("QR code generate failed", e);
		}
	}
}
