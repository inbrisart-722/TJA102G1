package com.eventra.cart_item.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventra.cart_item.model.AddCartItemReqDTO;
import com.eventra.cart_item.model.CartItemService;
import com.eventra.cart_item.model.GetCartItemResDTO;
import com.eventra.cart_item.model.GetMyExpirationResDTO;

@RestController
@RequestMapping("/api/front-end/protected/cartItem")
public class CartItemRestController {

	private final CartItemService CART_ITEM_SERVICE;
	
	private static final Integer TEST_MEMBER = 3;
	
	public CartItemRestController (CartItemService cartItemService){
		this.CART_ITEM_SERVICE = cartItemService;
	}
	
	@PostMapping("/addCartItem")
	public String addCartItem(@RequestBody AddCartItemReqDTO req) {
		try {CART_ITEM_SERVICE.addCartItem(req, TEST_MEMBER);}
		catch (IllegalStateException e) {
			System.out.println(e.toString());
			return "failure";
		}
		return "success";
	}
	// 前端給：exhibitionId, ticketDatas
	// 後端回：status ? 
	
	@DeleteMapping("/removeOneCartItem")
	public String removeOneCartItem(@RequestParam("cartItemId") Integer cartItemId) {
		return CART_ITEM_SERVICE.removeOneCartItem(cartItemId, TEST_MEMBER);
	}
	
	@DeleteMapping("/removeAllCartItem")
	public String removeCartItem() {
		return CART_ITEM_SERVICE.removeAllCartItem(TEST_MEMBER);
	}
	
	@GetMapping("/getAllCartItem")
	public List<GetCartItemResDTO> getAllCartItem() {
		return CART_ITEM_SERVICE.getAllCartItem(TEST_MEMBER);
	}
	
	@GetMapping("/getMyExpiration")
	public GetMyExpirationResDTO getMyExpiration() {
		return CART_ITEM_SERVICE.getEarliestExpiration(TEST_MEMBER);
	}
}
