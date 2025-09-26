package com.eventra.order.linepay.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.order.linepay.model.LinePayPaymentRequestReqDTO;
import com.eventra.order.linepay.model.LinePayService;

@RestController
@RequestMapping("/api/front-end/protected/linepay")
public class LinePayRestController {

	private final LinePayService LINE_PAY_SVC;
	
	public LinePayRestController(LinePayService linePayService) {
		this.LINE_PAY_SVC = linePayService;
	}
	
	@PostMapping("/payment-request")
	public ResponseEntity<String> paymentRequest(){
		
		LinePayPaymentRequestReqDTO.Product product = new LinePayPaymentRequestReqDTO.Product();
    	product.setName("product1")
    			.setQuantity(1)
    			.setPrice(1000);
    	
    	LinePayPaymentRequestReqDTO.Package pkg = new LinePayPaymentRequestReqDTO.Package();
    	pkg.setId("package1")
    		.setAmount(1000)
    		.setProducts(List.of(product));
    	
    	LinePayPaymentRequestReqDTO req = new LinePayPaymentRequestReqDTO();
    	req.setAmount(1000)
    		.setCurrency("TWD")
    		.setOrderId("order_testing_123")
    		.setPackages(List.of(pkg))
    		.setRedirectUrls(Map.of(
    				"confirmUrl", "http://localhost:8088/api/linepay/payment-request/confirm",
    				"cancelUrl", "http://localhost:8088/api/linepay/payment-request/cancel"
    				));
    	
    	String res = LINE_PAY_SVC.paymentRequest(req);
		return ResponseEntity.ok(res);
	}
	
	
}
