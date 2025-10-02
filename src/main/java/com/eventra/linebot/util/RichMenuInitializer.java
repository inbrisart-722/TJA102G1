package com.eventra.linebot.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RichMenuInitializer {

    @Value("${line.channel.access-token}")
    private String CHANNEL_ACCESS_TOKEN;

    private final HttpClient http = HttpClient.newHttpClient();

    /**
     * 一次性初始化：建立 + 上傳圖片 + 綁定所有使用者
     */
    public void initRichMenu(Path imagePath) throws Exception {
        // 1. 建立 Rich Menu
        String createBody = """
        {
          "size": { "width": 2500, "height": 843 },
          "selected": true,
          "name": "Default Rich Menu",
          "chatBarText": "選單",
          "areas": [
            {
              "bounds": { "x": 0, "y": 0, "width": 1250, "height": 843 },
              "action": { "type": "postback", "label": "熱門展覽", "data": "action=hot_exhibition" }
            },
            {
              "bounds": { "x": 1250, "y": 0, "width": 1250, "height": 843 },
              "action": { "type": "postback", "label": "最新展覽", "data": "action=new_exhibition" }
            }
          ]
        }
        """;

        HttpRequest createReq = HttpRequest.newBuilder()
                .uri(URI.create("https://api.line.me/v2/bot/richmenu"))
                .header("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(createBody))
                .build();

        HttpResponse<String> createResp = http.send(createReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("建立 RichMenu 回應: " + createResp.body());

        // 取出 richMenuId
        String richMenuId = extractRichMenuId(createResp.body());

        // 2. 上傳圖片
        HttpRequest uploadReq = HttpRequest.newBuilder()
                .uri(URI.create("https://api-data.line.me/v2/bot/richmenu/" + richMenuId + "/content"))
                .header("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN)
                .header("Content-Type", "image/png")
                .POST(HttpRequest.BodyPublishers.ofFile(imagePath))
                .build();

        HttpResponse<String> uploadResp = http.send(uploadReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("上傳圖片回應: " + uploadResp.body());

        // 3. 綁定所有使用者
        HttpRequest bindReq = HttpRequest.newBuilder()
                .uri(URI.create("https://api.line.me/v2/bot/user/all/richmenu/" + richMenuId))
                .header("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> bindResp = http.send(bindReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("綁定所有用戶回應: " + bindResp.body());
    }

    /**
     * 從建立回應 JSON 取出 richMenuId
     */
    private String extractRichMenuId(String responseBody) {
        // 超簡單處理方式（正式專案可用 Jackson 解析 JSON）
        int start = responseBody.indexOf("richMenuId") + 13;
        int end = responseBody.indexOf("\"", start);
        return responseBody.substring(start, end);
    }
}
