package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eventra.exhibitor.model.ExhibitorRepository;

@Controller
@RequestMapping("/")
public class PlatformIndexController {
	

	@GetMapping("/platform/login")
	public String login() {
		return "platform/login";
	}
	
	@GetMapping("/platform/comment")
	public String comment() {
		return "platform/comment";
	}
	
}
