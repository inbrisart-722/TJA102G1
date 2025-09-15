package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eventra.exhibitor.model.ExhibitorRepository;

@Controller
@RequestMapping("/")
public class PlatformIndexController {
	
	@Autowired
	ExhibitorRepository ep;

	@GetMapping("/platform/login")
	public String login() {
		return "platform/login";
	}
	
	@GetMapping("/back-end/exhibitor_login")
	public String login2() {
		return "back-end/exhibitor_login";
	}
	
	@GetMapping("/back-end/event_list")
	public String createEvent() {
//		System.out.println(ep.findByBusinessIdNumber(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().getCompanyName());
		return "back-end/event_list";
	}
	
	@GetMapping("/back-end/back_end_homepage")
	public String backEndHomepage() {
		return "back-end/back_end_homepage";
	}
}
