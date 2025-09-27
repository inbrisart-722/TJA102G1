package com.eventra.order.ecpay.model;

import java.sql.Timestamp;

public class ECPayCallbackReqDTO {

//	payment_attempt 實體跟回傳 DTO會「高度重疊」。但還是建議分開，因為職責不同：
	//	DTO（或 Map）：只負責「接收原始表單」→ 驗簽、驗證、一致性檢查
	//	Entity（payment_attempt）：只負責「可信資料的持久化」
//	即使欄位同名，也不要直接把 request 綁到 Entity，避免 over-posting、未驗簽先落庫。
	
	private String MerchantID;
	private String MerchantTradeNo;
	private String StoreID;
	private String RtnCode;
	private String RtnMsg;
	private String TradeNo;
	private Integer TradeAmt;
	private String PaymentDate;
	private String PaymentType;
	private Integer PaymentTypeChargeFee;
	private String TradeDate;
	private String PlatformId;
	private Byte SimulatePaid;
	private String CustomField1;
	private String CustomField2;
	private String CustomField3;
	private String CustomField4;
	private String CheckMacValue;
	
	public String getMerchantID() {
		return MerchantID;
	}
	public void setMerchantID(String merchantID) {
		MerchantID = merchantID;
	}
	public String getMerchantTradeNo() {
		return MerchantTradeNo;
	}
	public void setMerchantTradeNo(String merchantTradeNo) {
		MerchantTradeNo = merchantTradeNo;
	}
	public String getStoreID() {
		return StoreID;
	}
	public void setStoreID(String storeID) {
		StoreID = storeID;
	}
	public String getRtnCode() {
		return RtnCode;
	}
	public void setRtnCode(String rtnCode) {
		RtnCode = rtnCode;
	}
	public String getRtnMsg() {
		return RtnMsg;
	}
	public void setRtnMsg(String rtnMsg) {
		RtnMsg = rtnMsg;
	}
	public String getTradeNo() {
		return TradeNo;
	}
	public void setTradeNo(String tradeNo) {
		TradeNo = tradeNo;
	}
	public Integer getTradeAmt() {
		return TradeAmt;
	}
	public void setTradeAmt(Integer tradeAmt) {
		TradeAmt = tradeAmt;
	}
	public String getPaymentDate() {
		return PaymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		PaymentDate = paymentDate;
	}
	public String getPaymentType() {
		return PaymentType;
	}
	public void setPaymentType(String paymentType) {
		PaymentType = paymentType;
	}
	public Integer getPaymentTypeChargeFee() {
		return PaymentTypeChargeFee;
	}
	public void setPaymentTypeChargeFee(Integer paymentTypeChargeFee) {
		PaymentTypeChargeFee = paymentTypeChargeFee;
	}
	public String getTradeDate() {
		return TradeDate;
	}
	public void setTradeDate(String tradeDate) {
		TradeDate = tradeDate;
	}
	public String getPlatformId() {
		return PlatformId;
	}
	public void setPlatformId(String platformId) {
		PlatformId = platformId;
	}
	public Byte getSimulatePaid() {
		return SimulatePaid;
	}
	public void setSimulatePaid(Byte simulatePaid) {
		SimulatePaid = simulatePaid;
	}
	public String getCustomField1() {
		return CustomField1;
	}
	public void setCustomField1(String customField1) {
		CustomField1 = customField1;
	}
	public String getCustomField2() {
		return CustomField2;
	}
	public void setCustomField2(String customField2) {
		CustomField2 = customField2;
	}
	public String getCustomField3() {
		return CustomField3;
	}
	public void setCustomField3(String customField3) {
		CustomField3 = customField3;
	}
	public String getCustomField4() {
		return CustomField4;
	}
	public void setCustomField4(String customField4) {
		CustomField4 = customField4;
	}
	public String getCheckMacValue() {
		return CheckMacValue;
	}
	public void setCheckMacValue(String checkMacValue) {
		CheckMacValue = checkMacValue;
	}
	
//	10300028：「訂單編號重覆，建立失敗，請返回商店頁面重新下單。」
//	10300066：「交易付款結果待確認中，請勿出貨」，請至廠商管理後台確認已付款完成再出貨。
//	10100248：「拒絕交易，請客戶聯繫發卡行確認原因」
//	10100252：「額度不足，請客戶檢查卡片額度或餘額」
//	10100254：「交易失敗，請客戶聯繫發卡行確認交易限制」
//	10100251：「卡片過期，請客戶檢查卡片重新交易」
//	10100255：「報失卡，請客戶更換卡片重新交易」
//	10100256：「被盜用卡，請客戶更換卡片重新交易
	
	@Override
	public String toString() {
		return "ECPayCallbackReqDTO [MerchantID=" + MerchantID + ", MerchantTradeNo=" + MerchantTradeNo + ", StoreID="
				+ StoreID + ", RtnCode=" + RtnCode + ", RtnMsg=" + RtnMsg + ", TradeNo=" + TradeNo + ", TradeAmt="
				+ TradeAmt + ", PaymentDate=" + PaymentDate + ", PaymentType=" + PaymentType + ", PaymentTypeChargeFee="
				+ PaymentTypeChargeFee + ", TradeDate=" + TradeDate + ", PlatformId=" + PlatformId + ", SimulatePaid="
				+ SimulatePaid + ", CustomField1=" + CustomField1 + ", CustomField2=" + CustomField2 + ", CustomField3="
				+ CustomField3 + ", CustomField4=" + CustomField4 + ", CheckMacValue=" + CheckMacValue + "]";
	}
}
