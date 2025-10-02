package com.eventra.notificationpush.model;

/**
 * 傳輸用, 帶基本資訊
 * 用在推播或即時通知流程中, 如: WebSocket、STOMP、Line Bot 或其他即時通訊管道)
 * 保持 推播傳輸的格式 獨立, 不和 API DTO 混用
 *
 * 與 EventNotificationDTO 的差別
 * - NotificationMessageDTO: 包含基本通知資訊. 傳輸用
 * - EventNotificationDTO: 包含完整展覽與通知資訊, API 查詢用
 * 
 */

public class NotificationPushMessageDTO {

	private Integer memberId; 	// 會員ID
	private String title;		// 標題
	private String content;		// 內容
	private String type;		// 類型
	
	// 無參數建構子
	public NotificationPushMessageDTO() {
		super();
	}

	// 有參數建構子
	public NotificationPushMessageDTO(Integer memberId, String title, String content, String type) {
		super();
		this.memberId = memberId;
		this.title = title;
		this.content = content;
		this.type = type;
	}
	
	// getter / setter
	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
