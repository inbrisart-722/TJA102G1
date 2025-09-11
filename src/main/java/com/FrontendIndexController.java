package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eventra.favorite.model.FavoriteService;
import com.eventra.favorite.model.FavoriteVO;

@Controller
@RequestMapping("/front-end")
public class FrontendIndexController {

    @Autowired
    private FavoriteService favSvc;
    
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
    
    @GetMapping("/payment")
    public String paymentPage(Model model) {
    	return "front-end/payment";
    }
}