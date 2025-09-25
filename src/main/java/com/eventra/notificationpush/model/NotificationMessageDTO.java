package com.eventra.notificationpush.model;

public class NotificationMessageDTO {
	// 通知的資料格式
	
	private Integer memberId; 	// 會員ID
	private String title;		// 標題
	private String content;		// 內容
	private String type;		// 類型 (event / order)
//	boolean isRead; 			// 是否已讀
	
	// 無參數建構子
	public NotificationMessageDTO() {
		super();
	}

	// 有參數建構子
	public NotificationMessageDTO(Integer memberId, String title, String content, String type) {
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
