package com.eventra.notificationpush.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.eventnotification.util.EventNotificationFactory;
import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.notificationpush.model.NotificationService;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionNotificationController {
	// 靠 NotificationService 來發送通知

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ExhibitionRepository exhibitionRepo;

	// 發送展覽通知（由 type 決定通知內容）
	@PostMapping("/{id}/notify")
	public String notifyExhibition(@PathVariable("id") Integer exhibitionId, @RequestParam String type) {
		ExhibitionVO exh = exhibitionRepo.findById(exhibitionId)
				.orElseThrow(() -> new RuntimeException("展覽不存在，ID=" + exhibitionId));

		String title;
		String content;

		switch (type) {
		case "ticketStart":
			title = EventNotificationFactory.buildTicketStartTitle(exh.getExhibitionName());
			content = EventNotificationFactory.buildTicketStartContent(exh.getExhibitionName(), exh.getStartTime());
			break;
		case "ticketLow":
			title = EventNotificationFactory.buildTicketLowTitle(exh.getExhibitionName());
			content = EventNotificationFactory.buildTicketLowContent(exh.getExhibitionName(), 50 
					// TODO:
					// 這裡的剩餘票數要從票務系統查，先寫死
					// 50
			);
			break;
		case "locationChange":
			title = EventNotificationFactory.buildLocationChangeTitle(exh.getExhibitionName());
			content = EventNotificationFactory.buildLocationChangeContent(exh.getExhibitionName(), exh.getLocation());
			break;
		case "timeChange":
			title = EventNotificationFactory.buildTimeChangeTitle(exh.getExhibitionName());
			content = EventNotificationFactory.buildTimeChangeContent(exh.getExhibitionName(), exh.getStartTime(),
					exh.getEndTime());
			break;
		default:
			throw new IllegalArgumentException("未知通知類型: " + type);
		}

		notificationService.sendExhibitionNotification(exhibitionId, title, content);
		return "已通知收藏此展覽的會員，展覽ID=" + exhibitionId + "，類型=" + type;
	}
}
