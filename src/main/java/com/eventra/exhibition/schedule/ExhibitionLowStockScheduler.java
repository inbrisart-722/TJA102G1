package com.eventra.exhibition.schedule;

import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.notificationpush.model.NotificationPushService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * [低庫存提醒 的自動推播任務] 
 * 按照設定的 cron 定期執行, 自動檢查展覽剩餘票數
 * 若低於指定門檻 (threshold) 就會對收藏該展覽的會員發送 低庫存提醒通知
 *
 * 測試環境: 每5分鐘
 * 
 * NotificationPushService 負責推播
 * 
 */
@Component
public class ExhibitionLowStockScheduler {

    private final ExhibitionRepository exhibitionRepository;
    private final NotificationPushService notificationPushService;

    @Value("${eventra.notifications.lowStock.thresholds}")
    private String thresholdsConfig;

    public ExhibitionLowStockScheduler(
            ExhibitionRepository exhibitionRepository,
            NotificationPushService notificationPushService) {
        this.exhibitionRepository = exhibitionRepository;
        this.notificationPushService = notificationPushService;
    }

    @Scheduled(cron = "${eventra.notifications.lowStock.cron}")
    public void checkLowStock() {
        // 1. 解析 thresholds (ex: "50,30,20")
        String[] parts = thresholdsConfig.split(",");
        List<Integer> thresholds = new ArrayList<>();
        for (String p : parts) {
            p = p.trim();
            if (!p.isEmpty()) {
                thresholds.add(Integer.parseInt(p));
            }
        }

        // 2. 撈出所有展覽
        List<ExhibitionVO> exhibitions = exhibitionRepository.findAll();

        for (ExhibitionVO exh : exhibitions) {
            // 剩餘票數 = 總票數 - 已售出票數
            int leftTickets = exh.getTotalTicketQuantity() - exh.getSoldTicketQuantity();

            // 3. 判斷是否低於或等於門檻
            for (Integer threshold : thresholds) {
                if (leftTickets <= threshold) {
                    // 4. 呼叫推播服務 (帶 exhibitionId, 剩餘票數, threshold)
                    notificationPushService.sendLowStockNotification(
                            exh.getExhibitionId(),
                            leftTickets,
                            threshold
                    );
                }
            }
        }
    }
}