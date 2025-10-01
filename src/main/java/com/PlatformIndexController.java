package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eventra.exhibitor.model.ExhibitorRepository;
import com.eventra.platform_announcement.model.PlatformAnnouncementService;
import com.eventra.platform_announcement.model.PlatformAnnouncementVO;

@Controller
@RequestMapping("/")
public class PlatformIndexController {
	
	@Autowired
    PlatformAnnouncementService annSvc;

	@GetMapping("/platform/login")
	public String login() {
		return "platform/login";
	}
	
	@GetMapping("/platform/comment")
	public String comment() {
		return "platform/comment";
	}
	
	@GetMapping("/platform/index")
	public String index(Model model) {
		model.addAttribute("annListData", annSvc.getAll());
		return "platform/index";
	}
	
	@GetMapping("/platform/platform_edit")
	public String goAddForm(Model model) {
	    model.addAttribute("annVO", new PlatformAnnouncementVO());
	    return "platform/platform_edit";
	}
	
	@GetMapping("/platform/member")
	public String member() {
		return "platform/member";
	}
	
	@GetMapping("/platform/partner")
	public String partner() {
		return "platform/partner";
	}

	@GetMapping("/platform/report")
	public String report() {
		return "platform/report";
	}
	
	@GetMapping("/platform/exhibitor")
	public String exhibitor() {
		return "platform/exhibitor";
	}
	
	@GetMapping("/platform/exhibition")
	public String exhibition() {
		return "platform/exhibition";
	}

	
}
