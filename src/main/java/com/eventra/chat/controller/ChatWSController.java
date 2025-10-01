package com.eventra.chat.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.eventra.chat.model.ChatMessageReqDTO;
import com.eventra.chat.model.ChatMessageResDTO;
import com.eventra.chat.model.ChatService;

@Controller
// 專門處理 WebSocket STOMP 訊息的 Spring Controller
// 不是回傳 HTML，而是回傳訊息給 WebSocket broker

public class ChatWSController {

	private final ChatService CS_SVC;
	
	public ChatWSController(ChatService csService) { 
		this.CS_SVC = csService;
	}
	
	// 前端傳到 /app/chat
	// 後端再廣播到 /topic/messages
	
	@MessageMapping("/chat")
	// 類似於 @PostMapping，但不是處理 HTTP，而是處理來自 STOMP client 的 STOMP 訊息，回傳結果給 WebSocket broker
	// stompClient.send("/app/chat", {}, JSON.stringify(..)); 就會把訊息路由到此方法
	
	@SendTo("/topic/messages")
	// @SendTo -> 簡單用法
		// 適合 demo 或簡單的廣播邏輯
		// 你 return 什麼，它就直接丟到固定的 topic
	// simpMessagingTemplate.convertAndSend(...) -> 進階用法
	// simpMessagingTemplate.convertAndSendToUser(toUserId, ...)
		// 更靈活，可以在 service 裡隨時推播，不用等有人呼叫 controller
		// 例如：
			// 系統自動通知
			// 從 Redis 訂閱事件再廣播
			// 根據條件決定要送哪個 topic / 哪個 user
	
	// 可以不用加上 @Payload
		// 有 @Payload：Spring 會明確把 STOMP frame 的 body 映射到這個參數。
		// 沒有 @Payload：Spring 也會自動嘗試轉換第一個非特殊參數（不是 Principal / Authentication / MessageHeaders 這些）成訊息物件。
	public ChatMessageResDTO send(@Payload ChatMessageReqDTO req, Authentication auth, Principal principal) {
		// 在 Spring WebSocket + STOMP + Spring Security
		// 握手時會自動把目前登入、送出訊息的使用者（Authentication）包裝成一個 Principal，傳給 @MessageMapping 方法
		
		 boolean isMember = false;
		 for (var au : auth.getAuthorities()) {
			 if ("ROLE_MEMBER".equals(au.getAuthority())) {
				 isMember = true;
				 break;
			 }
		 }
		 
		Integer memberId = null; 
		if(principal != null && principal.getName() != null && isMember == true) memberId = Integer.valueOf(principal.getName());
		System.out.println("這次送訊息來的人的 memberId:" + memberId);
		System.out.println(auth != null ? auth.getName() : null);
			
		ChatMessageResDTO res = CS_SVC.addMessage(req, memberId);
		
		return res;
	}
	

//	public void? delete
	
}

// SockJS
	// WebSocket 的兼容層
	// 因為有些舊瀏覽器或網路環境（像公司 Proxy、防火牆）不支援 WebSocket，SockJS 就能自動 fallback 到其他方式
		// 例如長輪詢 Long Polling、XHR-streaming

// STOMP (Simple Text Oriented Messaging Protocol)
	// 一種簡單訊息協議，用來定義訊息格式＆路由方式
	// 本來 WebSocket 只會傳「原始資料／二進制資料」，不好管理
	// STOMP 會提供
		// 發送地址（/app/chat)
		// 訂閱地址（/topic/messages)
		// 訊息封包格式（header + body）
	// 就像是 WebSocket 上再包一層「聊天室專用規則」

// StompJS
	// JavaScript 的 STOMP 客戶端套件，讓前端能直接跟 Spring WebSocket（支援 STOMP 協議）對話

// 範例

// const socket = new SockJS("/ws-chat");
	// 後端設定的 endpoint -> WebSocketConfig 裡 registry.addEndpoint("/ws-chat")
	// SockJs 為 WebSocket 兼容層 ->「使用 WebSocket，但失敗就自動降級」的連線

// stompClient = Stomp.over(socket);
	// 代表在「SockJS 的連線」上再套用 STOMP 協議，這樣就能用
		// .connect()
		// .subscribe()
		// .send() 這些方法

// WebSocket 原始狀態
