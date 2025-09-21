package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	// 從 application.properties 讀取 google.api.key
	@Value("${google.api.key}")
    private String googleApiKey;
	
	@GetMapping("/admin")
	public String adminPage(Model model) {
		// 載入收藏
		model.addAttribute("favList", favSvc.findFavoritesByMember(1));
		return "front-end/admin";
	}

	// 1. 接住列表頁面 href: /front-end/exhibitions/
	@GetMapping("/exhibitions/{exhibitionId}")
	public String exhibitionsPageRedirect(@PathVariable("exhibitionId") Integer exhibitionId, Model model) {
		return "redirect:/front-end/exhibitions?exhibitionId=" + exhibitionId;
	}
	
	// 2. 為了同時確保 css, js 可取到，目前必要的轉導
//	@GetMapping("/exhibitions")
//	public String exhibitionsPage(@RequestParam("exhibitionId") Integer exhibitionId, Model model) {
//		System.out.println(exhibitionId);
//		return "front-end/exhibitions";
//	}
	
	// 0. 靜態測試（之後得刪）
	@GetMapping("/exhibitions")
	public String exhibitionsPageStatic(Model model) {
		return "front-end/exhibitions";
	}
	

	@GetMapping("/cart")
	public String cartPage(Model model, Authentication auth) {
//		System.out.println(auth);
//		System.out.println("Hello, " + auth.getName() + ": " + auth.getAuthorities());
//		System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
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
	public String loginPage(@AuthenticationPrincipal UserDetails user) {
		// 1. 使用者有帶 token，且有 MEMBER 身份，就不給進來登入頁面了，因為不然放他進來再次登入，要清 Token 再換發，不如就設計得先登出才放進來
		if(user != null && user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList()
				.contains("ROLE_MEMBER")) return "redirect:/front-end/index";
		
		// 2. 否則，歡迎！！！
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
    public String mapExplore(Model model) {
        model.addAttribute("googleApiKey", googleApiKey);
        return "front-end/map_explore";
    }
	
	@GetMapping("/search_results")
	public String searchResultsPage() {
		return "front-end/search_results";
	}
	
}