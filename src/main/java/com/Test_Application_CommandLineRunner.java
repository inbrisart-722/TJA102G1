package com;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.eventra.order.linepay.model.LinePayPaymentRequestReqDTO;
import com.eventra.order.linepay.model.LinePayPaymentRequestResDTO;
import com.eventra.order.linepay.model.LinePayService;

@SpringBootApplication
public class Test_Application_CommandLineRunner implements CommandLineRunner {

	@Autowired
	private LinePayService LINE_PAY_SVC; 
	
	public static void main(String[] args) {
        SpringApplication.run(Test_Application_CommandLineRunner.class);
    }

    @Override
    public void run(String...args) throws Exception {
    	
//    	LinePayPaymentRequestReqDTO.Product product = new LinePayPaymentRequestReqDTO.Product();
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
