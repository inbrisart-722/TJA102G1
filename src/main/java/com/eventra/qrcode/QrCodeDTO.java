package com.eventra.qrcode;

import java.time.LocalDateTime;

public class QrCodeDTO {
//	private Integer exhibitorId;
	private String exhibitorName;
	private String exhibitionName;
	private String memberNickname;
	private String ticketCode;
	private LocalDateTime qrCodeUsedAt;
	
	public String getExhibitorName() {
		return exhibitorName;
	}
	public void setExhibitorName(String exhibitorName) {
		this.exhibitorName = exhibitorName;
	}
	public String getExhibitionName() {
		return exhibitionName;
	}
	public void setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
	}
	public String getMemberNickname() {
		return memberNickname;
	}
	public void setMemberNickname(String memberNickname) {
		this.memberNickname = memberNickname;
	}
	public String getTicketCode() {
		return ticketCode;
	}
	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}
	public LocalDateTime getQrCodeUsedAt() {
		return qrCodeUsedAt;
	}
	public void setQrCodeUsedAt(LocalDateTime qrCodeUsedAt) {
		this.qrCodeUsedAt = qrCodeUsedAt;
	}
}
