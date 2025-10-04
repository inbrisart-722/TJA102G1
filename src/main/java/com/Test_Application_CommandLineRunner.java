package com;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
    	
    	
//    	LinePayPaymentRequestCheckResDTO res = LINE_PAY_SVC.paymentRequestCheck("2025093002306815010");
//    	System.out.println(res.getReturnCode());
//    	System.out.println(res.getReturnMessage());
    	
//    	LINE_PAY_SVC.paymentAuthorizationsVoid("LPMG604DGA7957");
    }
}
