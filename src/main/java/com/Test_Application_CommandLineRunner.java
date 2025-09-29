package com;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.eventra.linebot.util.RichMenuInitializer;

@SpringBootApplication
public class Test_Application_CommandLineRunner implements CommandLineRunner {

//	@Autowired
//	private LinePayService LINE_PAY_SVC; 
	
//	@Autowired
//	private LineBotPushService LINE_BOT_PUSH_SERVICE;
	
//	@Autowired
//	private RichMenuInitializer RICH_MENU_INITIALIZER;
	
//	@Value("${linepay.channel-id}") String channelId;
//	@Value("${linepay.channel-secret}") String channelSecret;
//	@Value("${linepay.base-url}") String baseUrl;
//	@Value("${linepay.confirm-url}") String confirmUrl;
//	@Value("${linepay.cancel-url}") String cancelUrl;
	
	public static void main(String[] args) {
        SpringApplication.run(Test_Application_CommandLineRunner.class);
    }

    @Override
    public void run(String...args) throws Exception {
    	
//    	LINE_BOT_PUSH_SERVICE.pushText("Ua69e70c501c86f90e191d1534708f631", "你很棒哦");
    	
//    	Path img = Paths.get("/Users/inbrisart/Desktop/img/S__34857062.jpg");
//    	RICH_MENU_INITIALIZER.initRichMenu(img);
    	
//    	System.out.println(channelId);
//    	System.out.println(channelSecret);
//    	System.out.println(baseUrl);
//    	System.out.println(confirmUrl);
//    	System.out.println(cancelUrl);
    	
//    	product.setName("product1")
//    			.setQuantity(1)
//    			.setPrice(1000);
//    	
//    	LinePayPaymentRequestReqDTO.Package pkg = new LinePayPaymentRequestReqDTO.Package();
//    	pkg.setId("package1")
//    		.setAmount(1000)
//    		.setProducts(List.of(product));
//    	
//    	LinePayPaymentRequestReqDTO req = new LinePayPaymentRequestReqDTO();
//    	req.setAmount(1000)
//    		.setCurrency("TWD")
//    		.setOrderId("order_testing_123")
//    		.setPackages(List.of(pkg))
//    		.setRedirectUrls(Map.of(
//    				"confirmUrl", "http://localhost:8088/api/linepay/payment-request/confirm",
//    				"cancelUrl", "http://localhost:8088/api/linepay/payment-request/cancel"
//    				));
//    	
//    	String res = LINE_PAY_SVC.paymentRequest(req);
//    	System.out.println(res);
    	
    }
}
