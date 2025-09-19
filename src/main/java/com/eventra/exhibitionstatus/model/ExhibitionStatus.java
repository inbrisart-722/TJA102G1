package com.eventra.exhibitionstatus.model;

public enum ExhibitionStatus {
	
	PENDING_REVIEW(1, "待審核"),
	REJECTED(2, "未通過審核"),
	UPCOMING(3, "尚未開賣"),
	ON_SALE(4, "售票中"),
	ENDED(5, "已結束");
	
	private final int code;
	private final String label;
	
	ExhibitionStatus(int code, String label){
		this.code = code;
		this.label = label;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getLabel() {
		return label;
	}
	
	public static ExhibitionStatus fromCode(int code) {
		for(ExhibitionStatus status : ExhibitionStatus.values()) {
			if(status.code == code) {
				return status;
			}
		}
		throw new IllegalArgumentException("未知的 code " + code);
	}

	public static ExhibitionStatus fromTime(java.time.LocalDateTime ticketStartTime, java.time.LocalDateTime endTime) {
		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		if(now.isBefore(ticketStartTime)) {
			return UPCOMING;
		}
		if(now.isAfter(endTime)) {
			return ENDED;
		}
		return ON_SALE;
	}
}
