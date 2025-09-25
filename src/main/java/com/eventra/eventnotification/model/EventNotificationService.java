package com.eventra.eventnotification.model;

import com.eventra.eventnotification.dto.EventNotificationDTO;
import com.eventra.eventnotification.util.EventNotificationFactory;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventNotificationService {

    @Autowired
    private EventNotificationRepository repo;

    @Autowired
    private ExhibitionRepository exhibitionRepo;

    // ===== 建立通知 =====
    public EventNotificationVO createNotification(Integer memberId,
                                                  Integer exhibitionId,
                                                  String title,
                                                  String content) {
        EventNotificationVO notif = new EventNotificationVO();
        notif.setMemberId(memberId);
        notif.setTitle(title);
        notif.setContent(content);
        notif.setReadStatus(false);
        return repo.save(notif);
    }

    // 使用 Factory + Exhibition 自動產生通知
    public EventNotificationVO createTicketStartNotification(Integer memberId, Integer exhibitionId) {
        ExhibitionVO exhibition = exhibitionRepo.findById(exhibitionId)
                .orElseThrow(() -> new RuntimeException("展覽不存在"));

        String title = EventNotificationFactory.buildTicketStartTitle(exhibition.getExhibitionName());
        String content = EventNotificationFactory.buildTicketStartContent(
                exhibition.getExhibitionName(),
                exhibition.getStartTime()
        );

        return createNotification(memberId, exhibitionId, title, content);
    }

    // ===== 查詢會員通知（補展覽資訊） =====
    public List<EventNotificationDTO> getMemberNotifications(Integer memberId) {
        return repo.findNotificationsByMember(memberId).stream()
                .map(vo -> {
                    // 嘗試找展覽資訊
                    Integer exhId = null;
                    String exhName = null;
                    String location = null;
                    String period = null;

                    if (vo.getFavoriteVO() != null) {
                        exhId = vo.getFavoriteVO().getExhibitionId();
                        ExhibitionVO exhibition = exhibitionRepo.findById(exhId).orElse(null);
                        if (exhibition != null) {
                            exhName = exhibition.getExhibitionName();
                            location = exhibition.getLocation();
                            period = exhibition.getStartTime() + " ~ " + exhibition.getEndTime();
                        }
                    }

                    return new EventNotificationDTO(
                            vo.getFavoriteAnnouncementId(),
                            exhId,
                            vo.getTitle(),
                            vo.getContent(),
                            vo.getReadStatus(),
                            vo.getCreatedAt(),
                            exhName,
                            location,
                            period
                    );
                })
                .collect(Collectors.toList());
    }

    // ===== 單筆已讀 =====
    public void markAsRead(Integer notifId) {
        repo.updateOneReadStatus(true, notifId);
    }

    // ===== 全部已讀 =====
    public void markAllAsRead(Integer memberId) {
        repo.updateAllReadStatus(true, memberId);
    }
}
