package com.eventra.linebot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.linebot.model.LineBotWebhookService;

@RestController
@RequestMapping("/api/front-end/linebot")
public class LineBotWebhookRestController {

	public final LineBotWebhookService LINE_WEBHOOK_SERVICE;

	public LineBotWebhookRestController(LineBotWebhookService lineWebhookService) {
		this.LINE_WEBHOOK_SERVICE = lineWebhookService;
	}

	// 只要一個端點 /webhook，因為 LINE webhook 是單一入口。
		// 只負責 接 webhook 請求。
		// 驗簽交給 service。
		// 不做商業邏輯，不負責組 JSON。
	@PostMapping("/webhook")
	public ResponseEntity<Void> webhook(@RequestHeader("X-Line-Signature") String signature, @RequestBody String body) {

		try {
			LINE_WEBHOOK_SERVICE.handleWebhook(signature, body);
			return ResponseEntity.ok().build();
		} catch (SecurityException e) {
			return ResponseEntity.status(403).build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

}

// @RequestBody String body 的結構（LINE 傳過來的 就是 webhook 的 JSON payload）

//	{
//	  "destination": "Uxxxxxxxxxxxxxx", 
//	  "events": [
//	    {
//	      "type": "message",
//	      "message": {
//	        "id": "1234567890123", 
//	        "type": "text",
//	        "text": "查展覽"
//	      },
//	      "timestamp": 1672531200000,
//	      "source": {
//	        "userId": "U4af4980629...",
//	        "type": "user"
//	      },
//	      "replyToken": "b60d432864f44d079f6d8efe86cf404b",
//	      "mode": "active"
//	    }
//	  ]
//	}

// destination: 是此 LINE 官方帳號的使用者 ID，如果此節點接很多官方帳號，就會需要它來分流
// events: 重點。一個陣列，裡面可能有多個 event
	// type: 事件型別。包含 message/ follow/ postback/ join/ leave ...
	// message: 如果是 message，會有具體內容（text/ image/ sticker ...)
		// message.id: // LINE 平台內 UNIQUE 的訊息 ID，可以用這個 messageId 呼叫 Get message content API 把原始訊息檔案下載回來
	// replyToken: 我們 server 回覆訊息一定要帶的 token（一次性，30秒內有效）
	// source：來源（userId, groupId, roomId) 
		// -> 如果使用者 沒加好友，可能拿不到 userId（會是空的）。
			// Messaging API 這邊，規則就是：加好友 → 你能拿到 userId。
			// 沒加好友 → 你可能只能拿到 groupId / roomId，拿不到個人 userId。
		// type: user, group（群組）, room(多人聊天室）
			// type == user -> source.userId 
			// type == group -> source.groupId + source + userId
			// type == room -> source.roomId + source.userId 
	// timestamp：發送時間
	// mode
		// active: 預設值。代表這個 event 是「一般正常訊息」，bot 可以照常處理。
		// standby: 基本上我們情境下可忽略。是在當 同一個 user 跟多個 bot 在同一個群組或聊天室時出現時才有差異。
			// 為了避免多個 bot 全部回應，LINE 規範：
			// 只有 一個 bot 會收到 active event，可以回應。
			// 其他 bot 會收到 standby event，不能回覆，只能記錄。


// 以下為 postback webhook JSON example

//	{
//	  "destination": "U1234567890abcdef1234567890abcdef",
//	  "events": [
//	    {
//	      "type": "postback",
//	      "mode": "active",
//	      "replyToken": "1234567890abcdef1234567890abcdef",
//	      "source": {
//	        "userId": "U4af4980629...",
//	        "type": "user"
//	      },
//	      "timestamp": 1672531200000,
//	      "postback": {
//	        "data": "action=queryOrders&page=1",
//	        "params": {
//	          "date": "2025-09-28"
//	        }
//	      }
//	    }
//	  ]
//	}
