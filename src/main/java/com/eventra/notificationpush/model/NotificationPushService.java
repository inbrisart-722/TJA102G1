package com.eventra.notificationpush.model;

import com.eventra.eventnotification.model.EventNotificationService;
import com.eventra.eventnotification.model.EventNotificationVO;
import com.eventra.eventnotification.model.EventNotificationService.NotificationType;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.favorite.model.FavoriteRepository;
import com.eventra.favorite.model.FavoriteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * [只負責 即時推播 (WebSocket / STOMP / Line Bot ...)]
 * 不處理資料庫存取 / 通知建立, 儲存功能全交給 EventNotificationService
 * 
 */

@Service
public class NotificationPushService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EventNotificationService eventNotificationService;
    
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    /**
     * 發送通知 (OPENING_SOON / TIME_CHANGE / LOCATION_CHANGE)
     */
    public void sendExhibitionNotification(Integer exhibitionId, NotificationType type) {
        Optional<ExhibitionVO> optionalExh = exhibitionRepository.findById(exhibitionId);
        if (!optionalExh.isPresent()) {
            throw new RuntimeException("展覽不存在, ID = " + exhibitionId);
        }
        ExhibitionVO exh = optionalExh.get();

        List<FavoriteVO> favorites = favoriteRepository.findByExhibitionId(exhibitionId);

        for (FavoriteVO fav : favorites) {
            if (fav.getFavoriteStatus() == null || fav.getFavoriteStatus() != 1) 
            	continue;

            EventNotificationVO entity = null;
            switch (type) {
                case OPENING_SOON:
                    entity = eventNotificationService.createTicketStartNotification(
                            fav.getFavoriteId(), fav.getMemberId(), exhibitionId);
                    break;
                case TIME_CHANGE:
                    entity = eventNotificationService.createTimeChangeNotification(
                            fav.getFavoriteId(), fav.getMemberId(), exhibitionId);
                    break;
                case LOCATION_CHANGE:
                    entity = eventNotificationService.createLocationChangeNotification(
                            fav.getFavoriteId(), fav.getMemberId(), exhibitionId);
                    break;
                default:
                    throw new IllegalArgumentException("未知通知類型: " + type);
            }

            if (entity != null) {
                messagingTemplate.convertAndSend(
                        "/topic/member/" + entity.getMemberId() + "/notifications",
                        new NotificationPushMessageDTO(
                                entity.getMemberId(),
                                entity.getTitle(),
                                entity.getContent(),
                                entity.getNotificationType()
                        )
                );
            }
        }
    }

    /**
     * 發送低庫存通知 (LOW_STOCK)
     */
    public void sendLowStockNotification(Integer exhibitionId, int remaining, int threshold) {
        Optional<ExhibitionVO> optionalExh = exhibitionRepository.findById(exhibitionId);
        if (!optionalExh.isPresent()) {
            throw new RuntimeException("展覽不存在, ID = " + exhibitionId);
        }
        ExhibitionVO exh = optionalExh.get();

        List<FavoriteVO> favorites = favoriteRepository.findByExhibitionId(exhibitionId);

        for (FavoriteVO fav : favorites) {
            if (fav.getFavoriteStatus() == null || fav.getFavoriteStatus() != 1) {
                continue;
            }

            // 傳 threshold
            EventNotificationVO entity = eventNotificationService.createLowStockNotification(
                    fav.getFavoriteId(),
                    fav.getMemberId(),
                    exhibitionId,
                    remaining,
                    threshold
            );

            if (entity != null) {
                messagingTemplate.convertAndSend(
                        "/topic/member/" + entity.getMemberId() + "/notifications",
                        new NotificationPushMessageDTO(
                                entity.getMemberId(),
                                entity.getTitle(),
                                entity.getContent(),
                                entity.getNotificationType()
                        )
                );
            }
        }
    }
}