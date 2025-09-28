package com.eventra.order.linepay.model;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.util.JsonCodec;

// 3層巢狀直接走 json 層級去寫此 dto

// https://developers-pay.line.me/zh/online-api-v3/request-payment
public class LinePayPaymentRequestReqDTO {
//	private Map<String, Object> options;
	
    private Integer amount;      // 總付款金額
    private String currency;     // 幣別 (ISO 4217) 測試 "TWD"
    private String orderId;
    private List<Package> packages; // 訂單明細（每筆明細又多 product)
    private Map<String, String> redirectUrls; // callback URL
    
    public LinePayPaymentRequestReqDTO() {}
    
    // package -> product : 像是訂單 -> 訂單明細
    
    public Integer getAmount() {
		return amount;
	}
	public LinePayPaymentRequestReqDTO setAmount(Integer amount) {
		this.amount = amount;
		return this;
	}
	public String getCurrency() {
		return currency;
	}
	public LinePayPaymentRequestReqDTO setCurrency(String currency) {
		this.currency = currency;
		return this;
	}
	public String getOrderId() {
		return orderId;
	}
	public LinePayPaymentRequestReqDTO setOrderId(String orderId) {
		this.orderId = orderId;
		return this;
	}
	public List<Package> getPackages() {
		return packages;
	}
	public LinePayPaymentRequestReqDTO setPackages(List<Package> packages) {
		this.packages = packages;
		return this;
	}
	public Map<String, String> getRedirectUrls() {
		return redirectUrls;
	}
	public LinePayPaymentRequestReqDTO setRedirectUrls(Map<String, String> redirectUrls) {
		this.redirectUrls = redirectUrls;
		return this;
	}
	
	public static class Package {
    	 private String id;       // 套裝ID (自行定義，如 "pkg001")
         private Integer amount;  // 套裝總金額
         private List<Product> products; // 商品明細
         
		public String getId() {
			return id;
		}
		public Package setId(String id) {
			this.id = id;
			return this;
		}
		public Integer getAmount() {
			return amount;
		}
		public Package setAmount(Integer amount) {
			this.amount = amount;
			return this;
		}
		public List<Product> getProducts() {
			return products;
		}
		public Package setProducts(List<Product> products) {
			this.products = products;
			return this;
		}
		
		 public static class Product {
		    	private String name;     // 商品名稱
		        private Integer quantity; // 數量
		        private Integer price;    // 單價
		        
				public String getName() {
					return name;
				}
				public Product setName(String name) {
					this.name = name;
					return this;
				}
				public Integer getQuantity() {
					return quantity;
				}
				public Product setQuantity(Integer quantity) {
					this.quantity = quantity;
					return this;
				}
				public Integer getPrice() {
					return price;
				}
				public Product setPrice(Integer price) {
					this.price = price;
					return this;
				}
		    }
    }
}

//curl -X POST \
//-H "Content-Type: application/json" \
//-H "X-LINE-ChannelId: YOUR_CHANNEL_ID" \
//-H "X-LINE-Authorization-Nonce: GENERATED_NONCE" \
//-H "X-LINE-Authorization: PROCESSED_SIGNATURE" \
//-H "X-LINE-MerchantDeviceProfileId: YOUR_DEVICE_PROFILE_ID" \
//-d '{
//     "amount" : 100,
//     "currency" : "TWD",
//     "orderId" : "MKSI_S_20180904_1000001",
//     "packages" : [
//       {
//         "id" : "1",
//         "amount": 100,
//         "products" : [
//           {
//             "id" : "PEN-B-001",
//             "name" : "Pen Brown",
//             "imageUrl" : "https://pay-store.example.com/images/pen_brown.jpg",
//             "quantity" : 2,
//             "price" : 50
//           }
//         ]
//       }
//     ],
//     "redirectUrls" : {
//       "confirmUrl" : "https://pay-store.example.com/order/payment/authorize",
//       "cancelUrl" : "https://pay-store.example.com/order/payment/cancel"
//     }
//   }'
//https://sandbox-api-pay.line.me/v3/payments/request
