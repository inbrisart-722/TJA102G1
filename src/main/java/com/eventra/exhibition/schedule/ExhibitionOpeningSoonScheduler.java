package com.eventra.exhibition.schedule;

import com.eventra.eventnotification.model.EventNotificationService.NotificationType;
import com.eventra.notificationpush.model.NotificationPushService;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * [開賣提醒 的自動推播任務] 
 * 按照設定的 cron 定期執行 自動檢查展覽票券開賣時間 (ticketStartTime)
 * 根據 application.properties 設定, 計算 "距離開賣剩下 N 小時", 符合條件就會對收藏該展覽的會員發送 開賣提醒
 * 
 * 測試環境: 每5分鐘
 *  
 * NotificationPushService 負責推播
 * 
 */

@Component
public class ExhibitionOpeningSoonScheduler {

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    @Autowired
    private NotificationPushService notificationService;

    @Value("#{'${eventra.notifications.openingSoon.leadHours}'.split(',')}")
    private List<Integer> leadHoursList;

    // 定時檢查 即將開賣 的展覽
    @Scheduled(cron = "${eventra.notifications.openingSoon.cron}")
    public void checkOpeningSoon() {
        // 1. 取得現在時間
        LocalDateTime now = LocalDateTime.now();

        // 2. 撈出所有展覽
        List<ExhibitionVO> exhibitions = exhibitionRepository.findAll();

        for (ExhibitionVO exh : exhibitions) {
            // 3. 略過沒有設定開賣時間的展覽
            if (exh.getTicketStartTime() == null) {
                continue;
            }

            // 4. 計算剩餘小時數
            long hoursUntilStart = Duration.between(now, exh.getTicketStartTime()).toHours();

            // 5. 判斷是否符合設定的提醒小時數
            for (Integer lead : leadHoursList) {
                if (hoursUntilStart == lead) {
                	
//                    System.out.println("[Scheduler] 展覽：" + exh.getExhibitionName() + "，距開賣 " + lead + " 小時，觸發通知");

                	// 6. 發送開賣提醒通知
                    notificationService.sendExhibitionNotification(
                            exh.getExhibitionId(),
                            NotificationType.OPENING_SOON
                    );
                }
            }
        }
    }
}