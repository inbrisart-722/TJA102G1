package com.eventra.notificationpush.controller;

import com.eventra.eventnotification.dto.EventNotificationDTO;
import com.eventra.eventnotification.model.EventNotificationService;
import com.eventra.eventnotification.model.EventNotificationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    @Autowired
    private EventNotificationService notificationService;

    // 撈出當前登入會員的通知
    @GetMapping("/my")
    public List<EventNotificationDTO> getMyNotifications() {
        Integer memId = Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        return notificationService.getMemberNotifications(memId);
    }

    // 單筆設為已讀
    @PostMapping("/{annId}/read")
    public String markAsRead(@PathVariable Integer annId) {
        notificationService.markAsRead(annId);
        return "已標記為已讀: " + annId;
    }

    // 全部設為已讀
    @PostMapping("/readAll")
    public String markAllAsRead() {
        Integer memId = Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        notificationService.markAllAsRead(memId);
        return "已標記會員 " + memId + " 的所有通知為已讀";
    }

    // 建立通知（範例用）
    @PostMapping("/create")
    public EventNotificationVO createNotification(@RequestParam Integer exhibitionId,
                                                 @RequestParam String title,
                                                 @RequestParam String content) {
        Integer memId = Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        return notificationService.createNotification(memId, exhibitionId, title, content);
    }
}
