package com.eventra.cart_item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("")
public class CartItemController {

	@GetMapping("front-end/cart")
	public String toPayment() {
		return "front-end/cart";
	}
	
}
