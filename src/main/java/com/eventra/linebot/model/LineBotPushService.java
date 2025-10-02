package com.eventra.linebot.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eventra.linebot.util.LineBotFlexBuilder;
import com.eventra.order.model.OrderLineBotCarouselDTO;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class LineBotPushService {

	private final HttpClient http = HttpClient.newHttpClient();

	private String CHANNEL_ACCESS_TOKEN;
	private final LineBotFlexBuilder FLEX_BUILDER = new LineBotFlexBuilder();
	private static final String PUSH_API = "https://api.line.me/v2/bot/message/push";

	public LineBotPushService(@Value("${line.channel.access-token}") String channelAccessToken) {
		this.CHANNEL_ACCESS_TOKEN = channelAccessToken;
	}

	public void pushExpiringCartItem(String lineUserId){
	System.out.println("LineBotPushService: pushExpiringCartItem");
      String json = """
      {
      		"to": "%s",
      		"messages": 
      		[
      			{
      			"type": "text",
      			"text": "%s"
      			}
      		]
      }

      """.formatted(lineUserId, "🔔提醒：您有購物車明細將於 5 分鐘內過期並釋票！請儘速前往結帳");
      try { send(json); }
      catch (Exception e) { System.out.println(e.toString()); }
    }

	public void pushOrder(String lineUserId, OrderLineBotCarouselDTO dto) throws Exception {
		ObjectNode bubble = FLEX_BUILDER.buildOrderBubble(dto);
		String json = FLEX_BUILDER.wrapFlexPush(lineUserId, bubble);
		send(json);
	}

	/**
	 * ✅ 單純推播文字
	 */
	public void pushText(String userId, String text) {
		try {
			String body = """
					{
					  "to": "%s",
					  "messages": [
					    {
					      "type": "text",
					      "text": "%s"
					    }
					  ]
					}
					""".formatted(userId, text);

			send(body);
		} catch (Exception e) {
			throw new RuntimeException("呼叫 LINE Push API 失敗", e);
		}
	}

	/**
	 * ✅ 推播多種訊息（文字 + 圖片 + 按鈕）
	 */
	public void pushRichMessage(String userId) {
		try {
			String body = """
					{
					  "to": "%s",
					  "messages": [
					    {
					      "type": "text",
					      "text": "您的訂單已成立！以下是票券資訊："
					    },
					    {
					      "type": "image",
					      "originalContentUrl": "https://example.com/ticket.png",
					      "previewImageUrl": "https://example.com/ticket-preview.png"
					    },
					    {
					      "type": "template",
					      "altText": "查看訂單詳情",
					      "template": {
					        "type": "buttons",
					        "text": "需要更多操作嗎？",
					        "actions": [
					          {
					            "type": "uri",
					            "label": "查看訂單",
					            "uri": "https://your-domain.com/orders/12345"
					          },
					          {
					            "type": "message",
					            "label": "聯繫客服",
					            "text": "我要聯繫客服"
					          }
					        ]
					      }
					    }
					  ]
					}
					""".formatted(userId);

			send(body);
		} catch (Exception e) {
			throw new RuntimeException("呼叫 LINE Push API 失敗", e);
		}
	}

	/**
	 * 共用的發送方法
	 */
	private void send(String body) throws Exception {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(PUSH_API))
				.header("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body)).build();

		HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println("LINE Push API 回應狀態: " + response.statusCode());
		System.out.println("LINE Push API 回應內容: " + response.body());
	}
}
