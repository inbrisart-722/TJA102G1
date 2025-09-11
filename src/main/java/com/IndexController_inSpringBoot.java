package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}