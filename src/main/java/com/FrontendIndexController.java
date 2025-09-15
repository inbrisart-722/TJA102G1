package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.cart_item.model.CartItemService;
import com.eventra.cart_item.model.GetCartItemResDTO;
import com.eventra.favorite.model.FavoriteService;

@Controller
@RequestMapping("/front-end")
public class FrontendIndexController {

	@Autowired
	private FavoriteService favSvc;
	
	@Autowired
	private CartItemService cartItemSvc;
	
	private static final Integer TEST_MEMBER = 3;

	@GetMapping("/admin")
	public String adminPage(Model model) {
		// 載入收藏
		model.addAttribute("favList", favSvc.findFavoritesByMember(1));
		return "front-end/admin";
	}

	@GetMapping("/exhibitions")
	public String exhibitionsPage(Model model) {
		return "front-end/exhibitions";
	}

	@GetMapping("/cart")
	public String cartPage(Model model) {
		return "front-end/cart";
	}

	@PostMapping("/payment")
	public String paymentPage(@RequestParam List<Integer> cartItemIds, Model model) {
		// 找到指定 cartItemDTOs
		System.out.println(cartItemIds);
		List<GetCartItemResDTO> listOfDTOs = cartItemSvc.getCartItem(TEST_MEMBER, cartItemIds);
		model.addAttribute("listOfDTOs", listOfDTOs);
		return "front-end/payment";
	}

	@GetMapping("/index")
	public String index() {
		return "front-end/index";
	}
	
	@GetMapping("/login")
	public String login() {
		return "front-end/login";
	}
	
	@GetMapping("/register1")
	public String register1() {
		return "front-end/register1";
	}
	
	@GetMapping("/register2")
	public String register2() {
		return "front-end/register2";
	}
	

}