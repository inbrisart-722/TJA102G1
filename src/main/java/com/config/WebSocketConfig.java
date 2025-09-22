package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
// 啟用 STOMP (Simple Text Oriented Messaging Protocol) 訊息代理功能
	// 意思是：後端會當作一個「中繼站」，幫前端處理「訂閱」「廣播」這種聊天室需求。
	// implements -> 客製化 WebSocket + STOMP 的行為
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	// 大綱
	// 1. 連線建立
		// 前端呼叫 SockJS("/ws-chat") -> 進入 registerStompEndpoints 定義的 endpoint
	// 2. 前端發送
		// 前端呼叫 stompClient.send("/app/chat", ...) -> 進入後端 @MessageMapping("/chat") 方法
	// 3. 後端廣播
		// 後端用 convertAndSend("/topic/messages", msg) -> 廣播給所有訂閱 /topic/messages 的前端
	// 4. 前端接收
		// 前端 stompClient.subscribe("/topic/messages", callback) -> 收到廣播訊息
		
	@Override
	// 這裡是「前端如何接進來」的設定
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws-chat")
		// 前端連線用的 WebSocket endpoint
		// 定義一個 WebSocket 連線入口 URL
		// 前端就會用 new SockJS("/ws-chat") 去連這個 endpoint
				.setAllowedOriginPatterns("*") 
				// Demo 可開放全部，正式要限制 domain 才行
				// CORS 設定，允許哪些網域可以來連接 WebSocket。
					// * 表示全部都行（正式上線記得鎖成自己的 domain）
				.withSockJS(); 
				// 可選：支援 SockJS fallback
				// 啟用 SockJS fallback 表示：
					// 如果使用者的瀏覽器或網路環境「不支援原生 WebSocket」，
					// 就會自動改用 HTTP long polling 模擬 WebSocket，
					// 確保服務相容性更好
	}
	
	@Override
	// 這裡是「前端怎麼發送、怎麼接收」的路徑規則
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		// 啟用一個 內建的簡易訊息代理（broker），處理「廣播」功能。
		// 規則是：凡是前端訂閱（subscribe）"/topic" 通道，後端都會透過 broker 廣播給所有訂閱者
		// 範例：
			// 前端：stompClient.subscribe("/topic/messages")
			// 後端：simpMessaingTemplate.convertAndSend("/topic/messages", payload);
			// 所有訂閱 "/topic/messages" 的人都會收到
		registry.setApplicationDestinationPrefixes("/app");
		// 定義「前端發送訊息」的路徑前綴
		// 規則是：凡是前端 send("/app/xxx") 的訊息，都會被後端的 @MessageMapping("/xxx") 方法處理
		// 範例：
			// 前端：stompClient.send("/app/chat", {}, json);
			// 後端：@MessageMapping("/chat") public void processMessage(ChatMessageDTO msg) {...}
	}
}
