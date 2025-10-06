package com;

import java.security.Principal;
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
import com.eventra.exhibition.model.ExhibitionPageDTO;
import com.eventra.exhibition.model.ExhibitionServiceImpl;
import com.eventra.exhibitor.model.ExhibitorService;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.member.verif.model.VerifService;
import com.eventra.exhibitioncommon.model.ExhibitionListService;
import com.eventra.exhibitioncommon.dto.ExhibitionListDTO;


import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/front-end")
public class FrontendIndexController {

	@Autowired
	private CartItemService cartItemSvc;
	@Autowired
	private VerifService verifSvc;
	@Autowired
	private ExhibitionServiceImpl exhibitionSvc;
	@Autowired
    private ExhibitorService exhibitorService;
	@Autowired
	private ExhibitionListService exhibitionListService;

	
//	private static final Integer TEST_MEMBER = 3;

	// 從 application.properties 讀取 google.api.key
	@Value("${google.api.key}") 
    private String googleApiKey;
	
	@GetMapping("/admin")
	public String adminPage() {
		return "front-end/admin";
	}
	
	// 1. 接住列表頁面 href: /front-end/exhibitions/
	@GetMapping("/exhibitions/{exhibitionId}")
	public String exhibitionsPageRedirect(@PathVariable("exhibitionId") Integer exhibitionId) {
		return "redirect:/front-end/exhibitions?exhibitionId=" + exhibitionId;
	}
	
	// 2. 為了同時確保 css, js 可取到，"目前"必要的轉導
	@GetMapping("/exhibitions")
	public String exhibitionsPage(@RequestParam("exhibitionId") Integer exhibitionId, Model model) {
		ExhibitionPageDTO dto = exhibitionSvc.getExhibitionInfoForPage(exhibitionId);
		// fallback -> exhibitionId 查不到對應展覽
//		if(dto == null) return "redirect:/front-end/404"; // 給 BasicErrorController 全專案去調用 !
		// success -> 塞 dto 並轉交 template-resolver
		model.addAttribute("exhibition", dto);
		return "front-end/exhibitions";
	}
	
	// 0. 靜態測試（之後得刪）
	@GetMapping("/exhibitions2")
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
	public String paymentPage(@RequestParam List<Integer> cartItemIds, Model model, Principal principal) {
		// 找到指定 cartItemDTOs
		Integer memberId = principal != null ? Integer.valueOf(principal.getName()) : null;
		List<GetCartItemResDTO> listOfDTOs = cartItemSvc.getCartItem(memberId, cartItemIds);
//		if(listOfDTOs == null || listOfDTOs.isEmpty()) return "front-end/payment?expired=true";
		model.addAttribute("listOfDTOs", listOfDTOs);
		return "front-end/payment";
	}

	@GetMapping("/index")
	public String index(@AuthenticationPrincipal UserDetails user, Model model) {
//		System.out.println(user.getUsername());
//		System.out.println(user.getPassword());
//		System.out.println(user.getAuthorities());
		
		// 取得每日隨機三個展覽(輪播用)
	    List<ExhibitionListDTO> dailyBanners = exhibitionListService.getDailyRandomThree();
	    model.addAttribute("dailyBanners", dailyBanners);
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
	public String loginPage(@AuthenticationPrincipal UserDetails user, HttpServletRequest req, Model model) {
		// 1. 使用者有帶 token，且有 MEMBER 身份，就不給進來登入頁面了，因為不然放他進來再次登入，要清 Token 再換發，不如就設計得先登出才放進來
		if(user != null && user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList()
				.contains("ROLE_MEMBER")) return "redirect:/front-end/index";
		
//		String OAuth2Redirect = req.getRequestURI();
//		model.addAttribute("OAuth2Redirect", OAuth2Redirect);
//		HttpSession session = req.getSession(true);
//		session.setAttribute("OAuth2Redirect", OAuth2Redirect);
				
		// 2. 否則，歡迎！！！
		return "front-end/login";
	}
	
	@GetMapping("/register1")
	public String register1Page() {
		return "front-end/register1";
	}
	
//	@GetMapping("/register2")
//	public String register2Page(@RequestParam("token") String token, Model model) {
//		String email = verifSvc.findEmailByToken(token);
//		model.addAttribute("email", email);
//		return "front-end/register2";
//	}
	
	@GetMapping("/forgot-password1")
	public String forgotPassword1Page() {
		return "front-end/forgot_password1";
	}
	
//	@GetMapping("/forgot-password2")
//	public String forgotPassword2Page(@RequestParam("token") String token, Model model) {
//		String email = verifSvc.findEmailByToken(token);
//		model.addAttribute("email", email);
//		return "front-end/forgot_password2";
//	}
	
	@GetMapping("/change-mail1")
	public String changeMail1Page() {
		return "front-end/change_mail1";
	}
	
	@GetMapping("/reset-password1")
	public String resetPassword1Page() {
		return "front-end/reset_password1";
	}
	
	@GetMapping("/verif-failure")
	public String temp() {
		return "front-end/verif_failure";
	}
	
	@GetMapping("/verif-registration-mail")
	public String verifRegistrationMail() {
		return "front-end/verif_registration_mail";
	}
	
//	@GetMapping("/exhibitor_register")
//	public String exhibitorRegister() {
//		return "front-end/exhibitor_register";
//	}
	
	@GetMapping("/map_explore")
    public String mapExplore(Model model) {
        model.addAttribute("googleApiKey", googleApiKey);
        return "front-end/map_explore";
    }
	
	@GetMapping("/search_results")
	public String searchResultsPage() {
		return "front-end/search_results";
	}
	
	// 比照展覽頁方式
	@GetMapping("/exhibitor_home/{exhibitorId}")
	public String exhibitorHomePageRedirect(@PathVariable("exhibitorId") Integer exhibitorId) {
	    return "redirect:/front-end/exhibitor_home?exhibitorId=" + exhibitorId;
	}

	@GetMapping("/exhibitor_home")
	public String exhibitorHomePage(@RequestParam("exhibitorId") Integer exhibitorId, Model model) {
	    ExhibitorVO exhibitor = exhibitorService.getExhibitorById(exhibitorId);
	    if (exhibitor == null) {
	        return "redirect:/front-end/404"; // 找不到展商就導 404
	    }
	    model.addAttribute("exhibitor", exhibitor);
	    return "front-end/exhibitor_home";
	}

}