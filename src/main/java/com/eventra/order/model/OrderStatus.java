package com.eventra.order.model;

//如果資料庫設計用 數字存狀態（例如 0=待付款, 1=付款中, 2=已付款），這就很實用。
//如果你要對接 外部系統 API，但對方只認數字代碼，也適合用這種設計。

public enum OrderStatus {
	付款中(1),
	付款失敗(2),
	付款逾時(3),
	已付款(4),
//	重複付款(5),
	已退款(5);
	
	private final int code;
	
	OrderStatus(int code) { this.code = code; }
	
	public int getCode() { return code; }
}

//付款中,
//付款失敗,
//付款逾時,
//已付款,
//重複付款,
//已退款;