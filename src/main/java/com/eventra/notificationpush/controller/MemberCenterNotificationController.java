package com.eventra.notificationpush.controller;

import com.eventra.eventnotification.dto.EventNotificationDTO;
import com.eventra.eventnotification.model.EventNotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [會員通知 API Controller] 會員中心用的 API
 * 
 * 1. GET /api/front-end/protected/notifications/my 			// 查詢當前登入會員的所有通知 
 * 2. POST /api/front-end/protected/notifications/{annId}/read 	// 單筆設為已讀 
 * 3. POST /api/front-end/protected/notifications/readAll 		// 所有通知設為已讀
 * 
 * ★ 需驗證 ROLE_MEMBER 權限 
 * ★ 通知資料由 EventNotificationService 提供
 * 
 */

@RestController
@RequestMapping("/api/front-end/protected/notifications")
public class MemberCenterNotificationController {

	@Autowired
	private EventNotificationService notificationService;

	// 撈出當前登入會員的通知
	@GetMapping("/my")
	public List<EventNotificationDTO> getMyNotifications(@AuthenticationPrincipal UserDetails user) {
		// 1. 確認登入帳號是會員角色
		boolean isMember = false;
		for (var auth : user.getAuthorities()) {
			if ("ROLE_MEMBER".equals(auth.getAuthority())) {
				isMember = true;
				break;
			}
		}

		if (!isMember) {
			throw new RuntimeException("不是會員帳號");
		}

		// 2. 轉成會員 ID
		Integer memId = Integer.valueOf(user.getUsername());

//	    System.out.println("member_id = " + memId);

		// 3. 繼續查詢通知
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
	public String markAllAsRead(@AuthenticationPrincipal UserDetails user) {
		boolean isMember = false;
		for (var auth : user.getAuthorities()) {
			if ("ROLE_MEMBER".equals(auth.getAuthority())) {
				isMember = true;
				break;
			}
		}
		if (!isMember) {
			throw new RuntimeException("不是會員帳號");
		}

		Integer memId = Integer.valueOf(user.getUsername());
		notificationService.markAllAsRead(memId);
		return "已標記會員 = " + memId + "  的所有通知為已讀";
	}

}
