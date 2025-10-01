package com.eventra.platform_announcement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.platform_announcement.model.PlatformAnnouncementService;
import com.eventra.platform_announcement.model.PlatformAnnouncementVO;

@Controller
@RequestMapping("/platform")
public class PlatformAnnouncementController {

	@Autowired
	PlatformAnnouncementService annSvc;	
	
	// 新增
//	@PostMapping("/save")
//	public String updateAnn(@RequestParam("annVO") PlatformAnnouncementVO annVO, Model model) {
//		PlatformAnnouncementVO ann = annSvc.updateAnn(annVO);
//		model.addAttribute("annVO", ann);
//		return "redirect:/platform/index";
//	}
	
	// 導入新增頁 (platformAnnouncementId = null)
	@GetMapping("/getOneList")
    public String AddList(Model model) {
        model.addAttribute("annVO", new PlatformAnnouncementVO()); // 空的，表示新增
        return "platform/platform_edit";
    }
	
	// 修改：先把資料帶到 form 頁面
	@GetMapping("/edit")
	public String editAnn(@RequestParam("platformAnnouncementId") Integer annId, Model model) {
	    PlatformAnnouncementVO annVO = annSvc.getOneAnn(annId);
	    model.addAttribute("annVO", annVO); // 帶出舊資料
	    return "platform/platform_edit";
	}

	
	// 修改
	@PostMapping("/save")
	public String saveAnn(@ModelAttribute PlatformAnnouncementVO annVO) {
        annSvc.saveAnn(annVO);
        return "redirect:/platform/index";
    }
	
	// 刪除
	@PostMapping("/delete")
	public String deleteAnn(@RequestParam("platformAnnouncementId") Integer annId) {
		annSvc.deleteByAnnId(annId);
		return "redirect:/platform/index";
	}

}
