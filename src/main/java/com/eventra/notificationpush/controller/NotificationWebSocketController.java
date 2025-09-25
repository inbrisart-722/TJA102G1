package com.eventra.notificationpush.controller;

import com.eventra.notificationpush.model.NotificationMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

// 專門處理 POST /api/notifications/send 這種 HTTP 請求
// 給 Postman / Ajax / 其他後端程式 呼叫的，不是 WebSocket 的測試入口
@RestController
@RequestMapping("/api/notifications")
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 測試推播通知
    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationMessageDTO dto) {
        messagingTemplate.convertAndSend(
            "/topic/member/" + dto.getMemberId() + "/notifications",
            dto
        );
        return "已發送通知給 memberId=" + dto.getMemberId();
    }
}
