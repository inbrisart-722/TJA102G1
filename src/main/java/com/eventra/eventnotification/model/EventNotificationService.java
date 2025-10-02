package com.eventra.eventnotification.model;

import com.eventra.eventnotification.dto.EventNotificationDTO;
import com.eventra.eventnotification.util.EventNotificationMessageBuilder;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

/**
 * [只負責 通知的建立與資料庫存取] 不處理推播, 推播功能全交給 NotificationPushService
 * 
 */

@Service
public class EventNotificationService {

	@Autowired
	private EventNotificationRepository eventNotificationRepo;

	@Autowired
	private ExhibitionRepository exhibitionRepo;

	// 通知類型 Enum
	public enum NotificationType {
		OPENING_SOON, // 開賣提醒
		LOW_STOCK, // 低庫存提醒
		TIME_CHANGE, // 檔期異動
		LOCATION_CHANGE // 地點異動
	}

	// 1. 建立通用通知
	public EventNotificationVO createNotification(Integer favoriteId, Integer memberId, Integer exhibitionId,
			NotificationType type, String title, String content) {
		EventNotificationVO notif = new EventNotificationVO();
		notif.setFavoriteId(favoriteId); // 收藏id
		notif.setMemberId(memberId); // 會員id
		if (exhibitionId == null) {
			throw new IllegalArgumentException("exhibitionId 不能為 null");
		}
		notif.setExhibitionId(exhibitionId); // 展覽id
		notif.setNotificationType(type.name()); // 通知類型
		notif.setTitle(title); // 標題
		notif.setContent(content); // 內容
		notif.setReadStatus(false); // 是否已讀
		return eventNotificationRepo.save(notif);
	}

	// 2. 開賣提醒
	public EventNotificationVO createTicketStartNotification(Integer favoriteId, Integer memberId,
			Integer exhibitionId) {
		ExhibitionVO exhibition = exhibitionRepo.findById(exhibitionId).orElseThrow(new Supplier<RuntimeException>() {
			@Override
			public RuntimeException get() {
				return new RuntimeException("展覽不存在");
			}
		});

		NotificationType type = NotificationType.OPENING_SOON;
		String title = EventNotificationMessageBuilder.buildTitleByType(type, exhibition.getExhibitionName());
		String content = EventNotificationMessageBuilder.buildTicketStartContent(exhibition.getExhibitionName(),
				exhibition.getStartTime());

		return createNotification(favoriteId, memberId, exhibitionId, type, title, content);
	}

	// 3. 低庫存提醒
	public EventNotificationVO createLowStockNotification(Integer favoriteId,
	                                                      Integer memberId,
	                                                      Integer exhibitionId,
	                                                      int remaining,      // 剩餘票數
	                                                      int threshold) {    // 通知門檻 (50/30/20)
	    ExhibitionVO exhibition = exhibitionRepo.findById(exhibitionId)
	            .orElseThrow(() -> new RuntimeException("展覽不存在"));

	    NotificationType type = NotificationType.LOW_STOCK;

	    // 標題 + 內容
	    String title = EventNotificationMessageBuilder.buildTitleByType(type, exhibition.getExhibitionName());
	    String content = EventNotificationMessageBuilder.buildTicketLowContent(
	            exhibition.getExhibitionName(),
	            threshold // 改成顯示門檻數
	    );

	    // 避免重複通知：同會員、同展覽、同類型、同門檻 不重複發
	    boolean exists = eventNotificationRepo.existsByMemberIdAndExhibitionIdAndNotificationTypeAndThreshold(
	            memberId, exhibitionId, type.name(), threshold
	    );

	    if (exists) {
	        return null;
	    }

	    // 共用欄位先塞
	    EventNotificationVO notif = createNotification(favoriteId, memberId, exhibitionId, type, title, content);

	    // 存門檻值
	    notif.setThreshold(threshold);

	    return eventNotificationRepo.save(notif);
	}


	// 4. 檔期異動
	public EventNotificationVO createTimeChangeNotification(Integer favoriteId, Integer memberId,
			Integer exhibitionId) {
		ExhibitionVO exhibition = exhibitionRepo.findById(exhibitionId).orElseThrow(new Supplier<RuntimeException>() {
			@Override
			public RuntimeException get() {
				return new RuntimeException("展覽不存在");
			}
		});

		NotificationType type = NotificationType.TIME_CHANGE;
		String title = EventNotificationMessageBuilder.buildTitleByType(type, exhibition.getExhibitionName());
		String content = EventNotificationMessageBuilder.buildTimeChangeContent(exhibition.getExhibitionName(),
				exhibition.getStartTime(), exhibition.getEndTime());

		return createNotification(favoriteId, memberId, exhibitionId, type, title, content);
	}

	// 5. 地點異動
	public EventNotificationVO createLocationChangeNotification(Integer favoriteId, Integer memberId,
			Integer exhibitionId) {
		ExhibitionVO exhibition = exhibitionRepo.findById(exhibitionId).orElseThrow(new Supplier<RuntimeException>() {
			@Override
			public RuntimeException get() {
				return new RuntimeException("展覽不存在");
			}
		});

		NotificationType type = NotificationType.LOCATION_CHANGE;
		String title = EventNotificationMessageBuilder.buildTitleByType(type, exhibition.getExhibitionName());
		String content = EventNotificationMessageBuilder.buildLocationChangeContent(exhibition.getExhibitionName(),
				exhibition.getLocation());

		return createNotification(favoriteId, memberId, exhibitionId, type, title, content);
	}

	// 6. 查詢會員通知列表
	// 分頁查詢會員通知列表
    public Page<EventNotificationDTO> getMemberNotifications(Integer memberId, Pageable pageable) {
        return eventNotificationRepo.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(vo -> {
                    ExhibitionVO exhibition = vo.getExhibition();
                    String exhName = exhibition != null ? exhibition.getExhibitionName() : null;
                    String location = exhibition != null ? exhibition.getLocation() : null;
                    String period = exhibition != null ? exhibition.getStartTime() + " ~ " + exhibition.getEndTime() : null;

                    return new EventNotificationDTO(
                            vo.getFavoriteAnnouncementId(),
                            vo.getExhibitionId(),
                            vo.getTitle(),
                            vo.getContent(),
                            vo.getReadStatus(),
                            vo.getCreatedAt(),
                            exhName,
                            location,
                            period
                    );
                });
    }
	
	
	
//	public List<EventNotificationDTO> getMemberNotifications(Integer memberId) {
//		List<EventNotificationVO> list = eventNotificationRepo.findNotificationsWithExhibition(memberId);
//
////		System.out.println("[EventNotificationService] memberId = " + memberId);
////		System.out.println("[EventNotificationService] DB筆數 = " + list.size());
//
//		List<EventNotificationDTO> dtoList = new java.util.ArrayList<>();
//
//		for (EventNotificationVO vo : list) {
//			ExhibitionVO exhibition = vo.getExhibition();
//
////	        System.out.println("[EventNotificationService] notifId=" + vo.getFavoriteAnnouncementId()
////	                + ", exhibitionId=" + exhId + ", title=" + vo.getTitle());
//
//			// 預設展覽資訊, 避免 NullPointerException
//			String exhName = null, location = null, period = null;
//			if (exhibition != null) {
//				exhName = exhibition.getExhibitionName(); // 展覽名稱
//				location = exhibition.getLocation(); // 展覽地點
//				period = exhibition.getStartTime() + " ~ " + exhibition.getEndTime(); // 展覽期間
//			}
//
//			// exhibitionId 改成保護式取值
//			Integer exhId = vo.getExhibitionId();
//			if (exhId == null && exhibition != null) {
//				exhId = exhibition.getExhibitionId();
//			}
//
//			// VO 轉成 DTO, 只保留要回傳前端的欄位
//			EventNotificationDTO dto = new EventNotificationDTO(vo.getFavoriteAnnouncementId(), // 通知ID
//					vo.getExhibitionId(), // 展覽ID
//					vo.getTitle(), // 標題
//					vo.getContent(), // 內容
//					vo.getReadStatus(), // 是否已讀
//					vo.getCreatedAt(), // 建立時間
//					exhName, // 展覽名稱
//					location, // 展覽地點
//					period // 展覽期間
//			);
//
//			dtoList.add(dto);
//		}
//
//		return dtoList;
//	}

	// 7. 單筆已讀
	public void markAsRead(Integer notifId) {
		eventNotificationRepo.updateOneReadStatus(true, notifId);
	}

	// 8. 全部已讀
	public void markAllAsRead(Integer memberId) {
		eventNotificationRepo.updateAllReadStatus(true, memberId);
	}
}
