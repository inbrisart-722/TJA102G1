package com.eventra.notificationpush.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.eventra.eventnotification.model.EventNotificationRepository;
import com.eventra.eventnotification.model.EventNotificationVO;
import com.eventra.favorite.model.FavoriteRepository;
import com.eventra.favorite.model.FavoriteVO;

import jakarta.transaction.Transactional;

@Service
public class NotificationService {
	// 「查詢收藏者 → 推播」的核心邏輯

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	// 測試用
//	public void sendExhibitionNotification(Integer exhibitionId, String title, String content) {
//		// 從收藏表找到該展覽的收藏紀錄
//		List<FavoriteVO> favorites = favoriteRepository.findByExhibitionId(exhibitionId);
//
//		// 過濾：只取收藏狀態 = 1 的
//		for (FavoriteVO fav : favorites) {
//			if (fav.getFavoriteStatus() != null && fav.getFavoriteStatus() == 1) {
//				Integer memberId = fav.getMemberId();
//
//				// WebSocket 推播到該會員的 topic
//				messagingTemplate.convertAndSend("/topic/member/" + memberId + "/notifications",
//						new NotificationMessageDTO(memberId, title, content, "event"));
//
//				// TODO: 存入 DB（下一步）
//				System.out.println("已發送給 memberId=" + memberId);
//			}
//		}
//	}

	@Autowired
	private EventNotificationRepository eventNotifiationRepository;

	@Transactional
	public void sendExhibitionNotification(Integer exhibitionId, String title, String content) {
		List<FavoriteVO> favorites = favoriteRepository.findByExhibitionId(exhibitionId);

		for (FavoriteVO fav : favorites) {
			if (fav.getFavoriteStatus() == null || fav.getFavoriteStatus() != 1)
				continue; // 只通知有效收藏

			Integer memberId = fav.getMemberId();

			// 存入 DB
			EventNotificationVO entity = new EventNotificationVO();
			entity.setFavoriteVO(fav);
			entity.setMemberId(memberId);
			entity.setTitle(title);
			entity.setContent(content);
			entity.setReadStatus(false);
			eventNotifiationRepository.save(entity);

			// WebSocket 推播
			messagingTemplate.convertAndSend("/topic/member/" + memberId + "/notifications",
					new NotificationMessageDTO(memberId, title, content, "event"));
		}

	}
}
