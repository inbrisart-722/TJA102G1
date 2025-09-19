package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.cart_item.model.CartItemService;
import com.eventra.cart_item.model.GetCartItemResDTO;
import com.eventra.favorite.model.FavoriteService;
import com.eventra.member.model.VerifService;

@Controller
@RequestMapping("/front-end")
public class FrontendIndexController {

	@Autowired
	private FavoriteService favSvc;
	
	@Autowired
	private CartItemService cartItemSvc;
	
	@Autowired
	private VerifService verifSvc;
	
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
	public String cartPage(Model model, Authentication auth) {
		System.out.println(auth);
		System.out.println("Hello, " + auth.getName() + ": " + auth.getAuthorities());
		System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
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
	public String index(@AuthenticationPrincipal UserDetails user) {
//		System.out.println(user.getUsername());
//		System.out.println(user.getPassword());
//		System.out.println(user.getAuthorities());
        return "front-end/index";
	}
	
	@GetMapping("/exhibitions_popular")
	public String exhibitionsPopularPage() {
		return "front-end/exhibitions_popular";
	}
	
	@GetMapping("/exhibitions_latest")
	public String exhibitionsLatestPage() {
		return "front-end/exhibitions_latest";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "front-end/login";
	}
	
	@GetMapping("/register1")
	public String register1Page() {
		return "front-end/register1";
	}
	
	@GetMapping("/register2")
	public String register2Page(@RequestParam("token") String token, Model model) {
		String email = verifSvc.findEmailByToken(token);
		model.addAttribute("email", email);
		return "front-end/register2";
	}
	
	@GetMapping("/verif-failure")
	public String temp() {
		return "front-end/verif_failure";
	}
	
	@GetMapping("/verif-registration-mail")
	public String verifRegistrationMail() {
		return "front-end/verif_registration_mail";
	}
	
	@GetMapping("/exhibitor_register")
	public String exhibitorRegister() {
		return "front-end/exhibitor_register";
	}
	
	@GetMapping("/map_explore")
	public String mapExplorePage() {
		return "front-end/map_explore";
	}
	
	@GetMapping("/search_results")
	public String searchResultsPage() {
		return "front-end/search_results";
	}
	
}