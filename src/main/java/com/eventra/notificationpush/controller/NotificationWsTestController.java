package com.eventra.notificationpush.controller;

import com.eventra.notificationpush.model.NotificationMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWsTestController {

//    // 前端 send("/app/sendTest", ...) 會進到這裡
//    @MessageMapping("/sendTest")
//    @SendTo("/topic/member/12/notifications") // 測試先寫死 memberId=12
//    public NotificationMessageDTO sendTest(NotificationMessageDTO message) {
//        System.out.println("收到前端測試訊息: " + message.getContent());
//        return message; // 會回傳到 /topic/member/12/notifications
//    }
    
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWsTestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 前端 send("/app/sendTest", ...) 就會進來
    @MessageMapping("/sendTest")
    public void sendTest(@Payload NotificationMessageDTO message) {
        System.out.println("收到前端測試訊息: " + message.getContent()); 

        // 動態推送到對應的 memberId topic
        messagingTemplate.convertAndSend(
            "/topic/member/" + message.getMemberId() + "/notifications",
            message
        );
    }
	
	
}
