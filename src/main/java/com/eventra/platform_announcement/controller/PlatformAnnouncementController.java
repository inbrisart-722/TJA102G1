package com.eventra.platform_announcement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.platform_announcement.model.PlatformAnnouncementService;
import com.eventra.platform_announcement.model.PlatformAnnouncementVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/platform")
public class PlatformAnnouncementController {

	@Autowired
	PlatformAnnouncementService annSvc;

	// 新增頁
	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("annVO", new PlatformAnnouncementVO());
		return "platform/platform_edit";
	}

	// 修改頁
	@GetMapping("/edit")
	public String editForm(@RequestParam("platformAnnouncementId") Integer annId, Model model) {
		PlatformAnnouncementVO annVO = annSvc.getOneAnn(annId);
		model.addAttribute("annVO", annVO);
		return "platform/platform_edit";
	}

	// 儲存 (新增或修改)
	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("annVO") PlatformAnnouncementVO annVO, BindingResult result) {

		System.out.println("收到內容：" + annVO.getContent());
		
		// 僅驗證標題，內容完全保留
		if (result.hasErrors()) {
			return "platform/platform_edit";
		}

		boolean isNew = (annVO.getPlatformAnnouncementId() == null);

		// 強制保留 HTML，不清理、不驗證
		annSvc.saveAnn(annVO);

		return isNew
				? "redirect:/platform/platform_edit?success=add"
				: "redirect:/platform/platform_edit?success=edit";
	}

	// 刪除
	@PostMapping("/delete")
	public String delete(@RequestParam("platformAnnouncementId") Integer annId) {
		annSvc.deleteByAnnId(annId);
		return "redirect:/platform/index?deleted=true";
	}

}
