package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.favorite.model.FavoriteService;

@Controller
@RequestMapping("/")
public class IndexController_inSpringBoot {

    @GetMapping("platform/index")
	public String select_page(Model model) {
		return "platform/index"; // 對應 templates/platform/index.html
	}

    @GetMapping("back-end/create_event")
	public String exhibitions(Model model) {
		return "back-end/create_event"; // 對應 templates/back-end/create_event.html
	}

    @Autowired
    private FavoriteService favSvc;
    
    @GetMapping("front-end/admin")
    public String adminPage(Model model) {
        model.addAttribute("favList", favSvc.findFavoritesByMember(1));
        return "front-end/admin";
    }

    @GetMapping("front-end/exhibitions")
    public String exhibitionsPage(Model model) {
        model.addAttribute("favList", favSvc.findFavoritesByMember(1));
        return "front-end/exhibitions";
    }
    
    @GetMapping("front-end/cart")
    public String cartPage() {
    	return "front-end/cart";
    }
    
    @PostMapping("front-end/payment")
    public String paymentPage(@RequestParam List<Integer> cartItemIds, Model model) {
    	// 找到指定 cartItemDTOs
    	System.out.println(cartItemIds);
    	
    	model.addAttribute("cartItemVOs", null);
    	return "front-end/payment";
    }
    
    @GetMapping("front-end/index")
    public String index() {
    	return "front-end/index";
    }
    
}