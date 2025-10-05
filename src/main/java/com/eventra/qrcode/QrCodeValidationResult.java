package com.eventra.qrcode;

public enum QrCodeValidationResult {
	EMPTY("缺少票券代碼 (ticketCode 欄位）"),
	NOT_EXIST("此 QR Code 對應之票號不存在！"),
	USED("此 QR Code 已經使用過囉！"),
	FORBIDDEN("您沒有權限驗證此 QR Code！"),
	VALID_BUT_NOT_START("票號存在且未使用過，但此展覽尚未開展"),
	VALID_BUT_END("票號存在且未使用過，但此展覽已經結束"),
	SUCCESS("恭喜驗證成功！歡迎入場！");
	
	 // 欄位
    private final String message;

    // 建構子
    QrCodeValidationResult(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }
}
