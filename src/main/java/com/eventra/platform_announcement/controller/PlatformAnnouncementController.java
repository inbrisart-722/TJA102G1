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
	public String save(@Valid @ModelAttribute("annVO") PlatformAnnouncementVO annVO, BindingResult result,
			Model model) {

		// 手動清理 HTML 標籤與空白內容
		String content = annVO.getContent();
		if (content != null) {
			content = content.replaceAll("<[^>]*>", ""); // 移除所有 HTML 標籤
			content = content.replaceAll("&nbsp;", ""); // 移除 &nbsp; (HTML 不斷行空白)
			content = content.trim(); // 去掉前後空白
		}

		// 內容錯誤驗證, 檢查內容是否為空
		if (content == null || content.isEmpty()) {
			result.rejectValue("content", "content.empty", "公告內容不可為空");
		}

		// 若有任何欄位錯誤, 回到編輯頁
		if (result.hasErrors()) {
			return "platform/platform_edit";
		}

		// 判斷是新增或修改
	    boolean isNew = (annVO.getPlatformAnnouncementId() == null);

	    annSvc.saveAnn(annVO);

	    // 成功後回傳對應訊息參數
	    if (isNew) {
	        return "redirect:/platform/platform_edit?success=add";
	    } else {
	        return "redirect:/platform/platform_edit?success=edit";
	    }
	}

	// 刪除
	@PostMapping("/delete")
	public String delete(@RequestParam("platformAnnouncementId") Integer annId) {
		annSvc.deleteByAnnId(annId);
		return "redirect:/platform/index?deleted=true";
	}

}
