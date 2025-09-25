package com.eventra.order.linepay.model;

// https://developers-pay.line.me/zh/online-api-v3/request-payment
public class LinePayPaymentRequestResDTO {
    private String returnCode; // 結果程式碼。API呼叫成功時，傳回0000值。其他結果碼均為錯誤碼
    private String returnMessage;  // 結果訊息。
    private Info info;        

    public static class Info { // 結果訊息
        private Long transactionId;  
        // LINE Pay 產生的交易 ID -> 可用於請求付款授權、取消授權和請款、退款。
        private PaymentUrl paymentUrl; // 前端導向的網址
        private String paymentAccessToken; // （可選）付款存取 token

        // getters/setters
        public Long getTransactionId() { return transactionId; }
        public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

        public PaymentUrl getPaymentUrl() { return paymentUrl; }
        public void setPaymentUrl(PaymentUrl paymentUrl) { this.paymentUrl = paymentUrl; }

        public String getPaymentAccessToken() { return paymentAccessToken; }
        public void setPaymentAccessToken(String paymentAccessToken) { this.paymentAccessToken = paymentAccessToken; }
    }

    public static class PaymentUrl { // 付款畫面confirmURL訊息
        private String web; 
        // 給網頁端 redirect
        // 重定向網頁URL。進入LINE Pay付款等待畫面的URL。如果顯示為彈出窗口，請將窗口大小設為寬度700px、高度546px。
        private String app; 
        // 給 LINE App 打開用
        // 深層連結confirmURL。Android從合作商店應用程式轉到LINE Pay應用程式的URL。

        // getters/setters
        public String getWeb() { return web; }
        public void setWeb(String web) { this.web = web; }

        public String getApp() { return app; }
        public void setApp(String app) { this.app = app; }
    }

    // getters/setters
    public String getReturnCode() { return returnCode; }
    public void setReturnCode(String returnCode) { this.returnCode = returnCode; }

    public String getReturnMessage() { return returnMessage; }
    public void setReturnMessage(String returnMessage) { this.returnMessage = returnMessage; }

    public Info getInfo() { return info; }
    public void setInfo(Info info) { this.info = info; }
    
	@Override
	public String toString() {
		return "LinePayPaymentRequestResDTO [returnCode=" + returnCode + ", returnMessage=" + returnMessage + ", info="
				+ info + "]";
	}
}

//{
//	  "returnCode": "0000",
//	  "returnMessage": "Success.",
//	  "info": {
//	    "paymentUrl": {
//	      "web": "https://sandbox-web-pay.line.me/web/payment/wait?transactionReserveId=REpEWEttQ0F2RmFnaFFzVndIdjl6Z0lqbGpPemZjOHpNWTFZTmdibUlRNlEzOG50N2VSRmdGU2IxcnVjMHZ1NQ",
//	      "app": "line://pay/payment/REpEWEttQ0F2RmFnaFFzVndIdjl6Z0lqbGpPemZjOHpNWTFZTmdibUlRNlEzOG50N2VSRmdGU2IxcnVjMHZ1NQ",
//	      "universal": "https://line.me/R/pay/payment/REpEWEttQ0F2RmFnaFFzVndIdjl6Z0lqbGpPemZjOHpNWTFZTmdibUlRNlEzOG50N2VSRmdGU2IxcnVjMHZ1NQ"
//	    },
//	    "transactionId": 2023042201206549310,
//	    "paymentAccessToken": "056579816895"
//	  }
//	}