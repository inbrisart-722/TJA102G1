package com;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController_inSpringBoot {

    @GetMapping("/platform/index")
	public String select_page(Model model) {
		return "platform/index"; // 對應 templates/platform/index.html
	}
    

    @GetMapping("/back-end/create_event")
	public String exhibitions(Model model) {
		return "back-end/create_event"; // 對應 templates/back-end/create_event.html
	}

}