package com.eventra.order.linepay.model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.util.JsonCodec;

@Service
@Transactional
public class LinePayService {

	@Value("${linepay.channel-id}")
	private static String CHANNEL_ID;
	@Value("${linepay.channel-secret}")
	private static String CHANNEL_SECRET;
	@Value("${linepay.base-url}")
	private static String BASE_URL;

	private final RestClient REST_CLIENT;
	private final JsonCodec JSON_CODEC;

	public LinePayService(RestClient.Builder restClientBuilder, JsonCodec jsonCodec) {
		this.REST_CLIENT = restClientBuilder.baseUrl(BASE_URL).build();
		this.JSON_CODEC = jsonCodec;
	}

	// 1. payment
	public String paymentRequest(LinePayPaymentRequestReqDTO req) {
		String apiPath = "/v3/payments/request";
		String nonce = UUID.randomUUID().toString();
		// 組簽章字串
		String message = CHANNEL_SECRET + apiPath + JSON_CODEC.write(req) + nonce;
		String signature = signHmacSHA256(CHANNEL_SECRET, message);

		
		LinePayPaymentRequestResDTO res =
				REST_CLIENT.post()
                .uri(apiPath)
                .header("Content-Type", "application/json")
                .header("X-LINE-ChannelId", CHANNEL_ID)
                .header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature)
                .body(req) // 給 Spring 轉 Json 即可
                .retrieve()
                .body(LinePayPaymentRequestResDTO.class);
		
		// 存 db
		
		return res.getInfo().getPaymentUrl().getWeb();
	}

	// 2. confirm request
	private String signHmacSHA256(String secret, String message) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] hmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hmac);
		} catch (Exception e) {
			throw new RuntimeException("HMAC generation failed", e);
		}
	}
}
