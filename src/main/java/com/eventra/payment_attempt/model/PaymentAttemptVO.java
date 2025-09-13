package com.eventra.payment_attempt.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.eventra.order.model.OrderVO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_attempt")
public class PaymentAttemptVO implements Serializable{

//	payment_attempt 實體跟回傳 DTO會「高度重疊」。但還是建議分開，因為職責不同：
	//	DTO（或 Map）：只負責「接收原始表單」→ 驗簽、驗證、一致性檢查
	//	Entity（payment_attempt）：只負責「可信資料的持久化」
//	即使欄位同名，也不要直接把 request 綁到 Entity，避免 over-posting、未驗簽先落庫。

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_attempt_id")
	private Integer paymentAttemptId;
	@Column(name = "payment_attempt_status")
	private String paymentAttemptStatus; // pending, expired, success, failure
	@Column(name = "order_id", insertable = false, updatable = false)
	private Integer orderId;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
	private OrderVO order;
	@Column(name = "merchant_id")
	private String merchantId;
	@Column(name = "merchant_trade_no") // "EC" + base36(System.currentTimeMillis()) + rand4
	private String merchantTradeNo;
	@Column(name = "store_id")
	private String storeId;
	@Column(name = "trade_no")
	private String tradeNo;
	@Column(name = "rtn_code")
	private String rtnCode;
	@Column(name = "rtn_msg")
	private String rtnMsg;
	@Column(name = "simulate_paid", columnDefinition = "TINYINT")
	private Byte simulatePaid;
	@Column(name = "trade_amt")
	private Integer tradeAmt;
	@Column(name = "payment_type")
	private String paymentType;
	@Column(name = "payment_type_charge_fee")
	private Integer paymentTypeChargeFee;
	@Column(name = "trade_date")
	private String tradeDate;
	@Column(name = "payment_date") // 用戶於綠界付完款的時間
	private String paymentDate;
	@Column(name = "check_mac_value")
	private String checkMacValue;
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt; 
	@Column(name = "item_name")
	private String itemName;
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Integer getPaymentAttemptId() {
		return paymentAttemptId;
	}
	public void setPaymentAttemptId(Integer paymentAttemptId) {
		this.paymentAttemptId = paymentAttemptId;
	}
	public String getPaymentAttemptStatus() {
		return paymentAttemptStatus;
	}
	public void setPaymentAttemptStatus(String paymentAttemptStatus) {
		this.paymentAttemptStatus = paymentAttemptStatus;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public OrderVO getOrder() {
		return order;
	}
	public void setOrder(OrderVO order) {
		this.order = order;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantTradeNo() {
		return merchantTradeNo;
	}
	public void setMerchantTradeNo(String merchantTradeNo) {
		this.merchantTradeNo = merchantTradeNo;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getRtnCode() {
		return rtnCode;
	}
	public void setRtnCode(String rtnCode) {
		this.rtnCode = rtnCode;
	}
	public String getRtnMsg() {
		return rtnMsg;
	}
	public void setRtnMsg(String rtnMsg) {
		this.rtnMsg = rtnMsg;
	}
	public Byte getSimulatePaid() {
		return simulatePaid;
	}
	public void setSimulatePaid(Byte simulatePaid) {
		this.simulatePaid = simulatePaid;
	}
	public Integer getTradeAmt() {
		return tradeAmt;
	}
	public void setTradeAmt(Integer tradeAmt) {
		this.tradeAmt = tradeAmt;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public Integer getPaymentTypeChargeFee() {
		return paymentTypeChargeFee;
	}
	public void setPaymentTypeChargeFee(Integer paymentTypeChargeFee) {
		this.paymentTypeChargeFee = paymentTypeChargeFee;
	}
	public String getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getCheckMacValue() {
		return checkMacValue;
	}
	public void setCheckMacValue(String checkMacValue) {
		this.checkMacValue = checkMacValue;
	}
	
//	10300028：「訂單編號重覆，建立失敗，請返回商店頁面重新下單。」
//	10300066：「交易付款結果待確認中，請勿出貨」，請至廠商管理後台確認已付款完成再出貨。
//	10100248：「拒絕交易，請客戶聯繫發卡行確認原因」
//	10100252：「額度不足，請客戶檢查卡片額度或餘額」
//	10100254：「交易失敗，請客戶聯繫發卡行確認交易限制」
//	10100251：「卡片過期，請客戶檢查卡片重新交易」
//	10100255：「報失卡，請客戶更換卡片重新交易」
//	10100256：「被盜用卡，請客戶更換卡片重新交易」
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	// 送出前先 build 基本資訊
	public static class Builder {
		private String paymentAttemptStatus;
		private OrderVO order;
		private String merchantTradeNo;
		private String merchantId;
		private Integer tradeAmt;
//		private String paymentType;
		private String tradeDate;
		private String itemName;

		public Builder paymentAttemptStatus(String paymentAttemptStatus) {
			this.paymentAttemptStatus = paymentAttemptStatus;
			return this;
		}
		
		public Builder order(OrderVO order) {
			this.order = order;
			return this;
		}

		public Builder merchantTradeNo(String merchantTradeNo) {
			this.merchantTradeNo = merchantTradeNo;
			return this;
		}

		public Builder merchantId(String merchantId) {
			this.merchantId = merchantId;
			return this;
		}

		public Builder tradeAmt(Integer tradeAmt) {
			this.tradeAmt = tradeAmt;
			return this;
		}

//		public Builder paymentType(String paymentType) {
//			this.paymentType = paymentType;
//			return this;
//		}
		
		public Builder tradeDate(String tradeDate) {
			this.tradeDate = tradeDate;
			return this;
		}
		
		public Builder itemName(String itemName) {
			this.itemName = itemName;
			return this;
		}

		public PaymentAttemptVO build() {
			PaymentAttemptVO vo = new PaymentAttemptVO();
			vo.paymentAttemptStatus = paymentAttemptStatus;
			vo.order = this.order;
			vo.merchantTradeNo = this.merchantTradeNo;
			vo.merchantId = this.merchantId;
			vo.tradeAmt = this.tradeAmt;
//			vo.paymentType = this.paymentType;
			vo.tradeDate = this.tradeDate;
			vo.itemName = this.itemName;
			return vo;
		}
	}
}
