package com.eventra.payment_attempt.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.eventra.order.model.OrderVO;
import com.eventra.order.model.OrderProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	/* **************** (1) ECPay & LinePay Common **************** */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_attempt_id")
	private Integer paymentAttemptId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_attempt_status")
	private PaymentAttemptStatus paymentAttemptStatus; // PENDING, EXPIRED, SUCCESS, FAILURE
	
	@Column(name = "order_id", insertable = false, updatable = false)
	private Integer orderId;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
	private OrderVO order;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "provider")
	private OrderProvider provider;
	// ECPay
	// LinePay
	
	@Column(name = "provider_order_id") 
	private String providerOrderId;
	// ECPay MerchantTradeNo "EC" + base36(System.currentTimeMillis()) + rand4
	// LinePay orderId
	
	@Column(name = "provider_transaction_id") 
	private String providerTransactionId;
	// ECPay tradeNo
	// LinePay transactionId
	
	@Column(name = "trade_amt")
	private Integer tradeAmt;
	// ECPay tradeAmt
	// LinePay amount
	
	@Column(name = "rtn_code")
	private String rtnCode;
	// ECPay rtnCode
	// LinePay returnCode
	
	@Column(name = "rtn_msg")
	private String rtnMsg;
	// ECPay rtnMsg
	// LinePay returnMessage
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt; 
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
	
	/* **************** (2) LinePay-specific **************** */
	@Column(name = "currency")
	private String currency; // "TWD"
	
	@Column(name = "packages_json", columnDefinition = "TEXT")
	private String packagesJson;
	 
	/* **************** (3) ECPay-specific **************** */
	@Column(name = "merchant_id")
	private String merchantId;
	@Column(name = "store_id")
	private String storeId;
	@Column(name = "simulate_paid", columnDefinition = "TINYINT")
	private Byte simulatePaid;
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
	@Column(name = "item_name")
	private String itemName;
	
	public Integer getPaymentAttemptId() {
		return paymentAttemptId;
	}
	public void setPaymentAttemptId(Integer paymentAttemptId) {
		this.paymentAttemptId = paymentAttemptId;
	}
	public PaymentAttemptStatus getPaymentAttemptStatus() {
		return paymentAttemptStatus;
	}
	public void setPaymentAttemptStatus(PaymentAttemptStatus paymentAttemptStatus) {
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
	public OrderProvider getProvider() {
		return provider;
	}
	public void setProvider(OrderProvider provider) {
		this.provider = provider;
	}
	public String getProviderOrderId() {
		return providerOrderId;
	}
	public void setProviderOrderId(String providerOrderId) {
		this.providerOrderId = providerOrderId;
	}
	public String getProviderTransactionId() {
		return providerTransactionId;
	}
	public void setProviderTransactionId(String providerTransactionId) {
		this.providerTransactionId = providerTransactionId;
	}
	public Integer getTradeAmt() {
		return tradeAmt;
	}
	public void setTradeAmt(Integer tradeAmt) {
		this.tradeAmt = tradeAmt;
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
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getPackagesJson() {
		return packagesJson;
	}
	public void setPackagesJson(String packagesJson) {
		this.packagesJson = packagesJson;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public Byte getSimulatePaid() {
		return simulatePaid;
	}
	public void setSimulatePaid(Byte simulatePaid) {
		this.simulatePaid = simulatePaid;
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
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}


	// 送出前先 build 基本資訊
	public static class Builder {
		private PaymentAttemptStatus paymentAttemptStatus;
		private OrderVO order;
		private OrderProvider provider;
		private String providerOrderId;
		private Integer tradeAmt;
		
		// LinePay-specific（此處）
		private String currency;
		private String packagesJson;
		private String providerTransactionId;
		
		// ECPay-specific（此處）
		private String itemName;
		private String tradeDate;
		private String merchantId;

		// (1) Common 
		public Builder paymentAttemptStatus(PaymentAttemptStatus paymentAttemptStatus) {
			this.paymentAttemptStatus = paymentAttemptStatus;
			return this;
		}
		public Builder order(OrderVO order) {
			this.order = order;
			return this;
		}
		public Builder provider(OrderProvider provider) {
			this.provider = provider;
			return this;
		}
		public Builder providerOrderId(String providerOrderId) {
			this.providerOrderId = providerOrderId;
			return this;
		}
		public Builder tradeAmt(Integer tradeAmt) {
			this.tradeAmt = tradeAmt;
			return this;
		}
		// (2) LinePay-specific
		public Builder currency(String currency) {
			this.currency = currency;
			return this;
		}
		public Builder packagesJson(String packagesJson) {
			this.packagesJson = packagesJson;
			return this;
		}
		public Builder providerTransactionId(String providerTransactionId) {
			this.providerTransactionId = providerTransactionId;
			return this;
		}
		// (3) ECPay-specific
		public Builder itemName(String itemName) {
			this.itemName = itemName;
			return this;
		}
		public Builder tradeDate(String tradeDate) {
			this.tradeDate = tradeDate;
			return this;
		}
		public Builder merchantId(String merchantId) {
			this.merchantId = merchantId;
			return this;
		}

		public PaymentAttemptVO buildECPay() {
			PaymentAttemptVO vo = new PaymentAttemptVO();
			vo.paymentAttemptStatus = paymentAttemptStatus;
			vo.order = this.order;
			vo.provider = this.provider;
			vo.providerOrderId = this.providerOrderId;
			vo.tradeAmt = this.tradeAmt;
			
			vo.itemName = this.itemName;
			vo.tradeDate = this.tradeDate;
			vo.merchantId = this.merchantId;
			return vo;
		}
		
		public PaymentAttemptVO buildLinePay() {
			PaymentAttemptVO vo = new PaymentAttemptVO();
			vo.paymentAttemptStatus = paymentAttemptStatus;
			vo.order = this.order;
			vo.provider = this.provider;
			vo.providerOrderId = this.providerOrderId;
			vo.tradeAmt = this.tradeAmt;
			
			vo.currency = this.currency;
			vo.packagesJson = this.packagesJson;
			vo.providerTransactionId = this.providerTransactionId;
			return vo;
		}
	}
}
