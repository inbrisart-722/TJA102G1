package com.eventra.order.scheduler;

public class ECPayQueryScheduler {

}

// ReturnURL 是「交易結論」的通知，不是「每次進金流」的通知。
// 如果交易沒有結論（例如用戶自己中斷、驗證失敗沒授權成功），那時候不會立即給 ReturnURL。
// 所以你系統要做兩件事：
//   ReturnURL → 當成最權威依據，有來就一定處理。
//   ClientBackURL/OrderResultURL → 也要接，因為有些中斷/驗證失敗的狀況 ReturnURL 不會來。
//   定期查詢 API (QueryTradeInfo)，補強「漏單」的情境（例如用戶跳出沒付款，訂單就掛著）。