package com.line;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/front-end/line")
public class LineWebhookRestController {

	@Value("${line.channel.secret}")
	private String CHANNEL_SECRET;
	@Value("${line.channel.access-token}")
	private String CHANNEL_ACCESS_TOKEN;

	private final ObjectMapper OM = new ObjectMapper();
	private final HttpClient http = HttpClient.newHttpClient();


	@PostMapping("/webhook")
	public ResponseEntity<Void> webhook(@RequestHeader("X-Line-Signature") String signature, @RequestBody String body)
			throws Exception {

		if (!verifySignature(body, signature, CHANNEL_SECRET))
			return ResponseEntity.status(403).build();

		var root = OM.readTree(body);
		for (JsonNode ev : root.withArray("events")) {
			String type = ev.path("type").asText();
			if ("message".equals(type)) {
				String replyToken = ev.path("replyToken").asText();
				replyWithQuickReply(replyToken, "歡迎來到 TJA102 - Eventra！您今天想使用哪項服務呢？");
			}
		}
		return ResponseEntity.ok().build();
	}

	void replyWithQuickReply(String replyToken, String text) throws Exception {
        String json = """
        {
          "replyToken": "%s",
          "messages": [{
            "type": "text",
            "text": "%s",
            "quickReply": {
              "items": [
                { "type": "action",
                  "action": { "type": "message", "label": "查展覽", "text": "查展覽" } },
                { "type": "action",
                  "action": { "type": "message", "label": "我的訂單", "text": "我的訂單" } }
              ]
            }
          }]
        }
        """.formatted(replyToken, text);

        HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create("https://api.line.me/v2/bot/message/reply"))
          .header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN)
          .POST(HttpRequest.BodyPublishers.ofString(json))
          .build();

        http.send(req, HttpResponse.BodyHandlers.discarding());
    }
	boolean verifySignature(String body, String signature, String channelSecret) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(channelSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
		String computed = Base64.getEncoder().encodeToString(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
		return MessageDigest.isEqual(computed.getBytes(StandardCharsets.UTF_8),
				signature.getBytes(StandardCharsets.UTF_8));
	}

}
