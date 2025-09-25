//package com.eventra.notificationpush.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic"); // 前端訂閱用
//        config.setApplicationDestinationPrefixes("/app"); // 前端發送用（如果要支援 /app/xxx）
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws") // WebSocket 連線端點
//                .setAllowedOriginPatterns("*") // 允許跨域
//                .withSockJS(); // 支援 SockJS（防止瀏覽器不支援原生 WS）
//    }
//}
