package com.eventra.notificationpush.controller;

import com.eventra.notificationpush.model.NotificationPushMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * [WebSocket 測試用 Controller]
 * 提供 HTTP API (POST /api/notifications/send) 測試通知推播
 * 
 * 用 SimpMessagingTemplate 直接將訊息推播到 WebSocket 訂閱的 Topic, 不會存資料庫, 
 * 正式通知是透過 NotificationPushService
 * 
 */

@RestController
@RequestMapping("/api/notifications")
public class NotificationWebSocketTestController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationWebSocketTestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationPushMessageDTO dto) {
        messagingTemplate.convertAndSend(
            "/topic/member/" + dto.getMemberId() + "/notifications",
            dto
        );
        return "已發送通知給 member_id = " + dto.getMemberId();
    }
}
