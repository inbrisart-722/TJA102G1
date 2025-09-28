package com.eventra.linebot.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LineBotPushService {

    private final HttpClient http = HttpClient.newHttpClient();

    @Value("${line.channel.access-token}")
    private String CHANNEL_ACCESS_TOKEN;

    private static final String PUSH_API = "https://api.line.me/v2/bot/message/push";

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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PUSH_API))
                .header("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("LINE Push API 回應狀態: " + response.statusCode());
        System.out.println("LINE Push API 回應內容: " + response.body());
    }
}
