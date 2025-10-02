package com.eventra.notificationpush.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.eventnotification.model.EventNotificationService.NotificationType;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.notificationpush.model.NotificationPushService;

/**
 * [展覽通知觸發 Controller]
 * 
 * 1. POST /api/exhibitions/{id}/notify?type=xxx
 *    - 支援的 type: ticketStart, ticketLow, locationChange, timeChange
 *    - 會轉換為 NotificationType Enum
 *    - 驗證展覽存在後, 交給 NotificationPushService 建立通知並推播
 * 
 */

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionNotificationController {

    @Autowired
    private NotificationPushService notificationService;

    @Autowired
    private ExhibitionRepository exhibitionRepo;

    // 發送展覽通知
    @PostMapping("/{id}/notify")
    public String triggerExhibitionNotification(
            @PathVariable("id") Integer exhibitionId,
            @RequestParam String type) {

        // 1. 確認展覽存在
        boolean exists = exhibitionRepo.existsById(exhibitionId);
        if (!exists) {
            throw new RuntimeException("展覽不存在, ID = " + exhibitionId);
        }

        // 2. String >> Enum
        NotificationType typeEnum;
        switch (type) {
            case "ticketStart":
                typeEnum = NotificationType.OPENING_SOON;		// 開賣提醒
                break;
            case "ticketLow":
                typeEnum = NotificationType.LOW_STOCK;			// 低庫存提醒
                break;
            case "locationChange":
                typeEnum = NotificationType.LOCATION_CHANGE;	// 地點異動
                break;
            case "timeChange":
                typeEnum = NotificationType.TIME_CHANGE;		// 展期異動
                break;
            default:
                try {
                    typeEnum = NotificationType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("未知通知類型: " + type);
                }
        }

        // 3. 呼叫推播
        notificationService.sendExhibitionNotification(exhibitionId, typeEnum);

        return "已通知收藏此展覽的會員, 展覽ID = " + exhibitionId + "，類型 = " + typeEnum;
    }
}