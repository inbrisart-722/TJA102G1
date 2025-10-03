package com.eventra.exhibitor.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eventra.exhibitor.model.ExhibitorRegisterForm;
import com.eventra.exhibitor.model.ExhibitorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/front-end")
public class ExhibitorRegisterController {

	private final ExhibitorService EXHIBITOR_SVC;
	
	public ExhibitorRegisterController(ExhibitorService exhibitorService) {
		this.EXHIBITOR_SVC = exhibitorService;
	}
	
	@GetMapping("/exhibitor_register")
	public String registerPage(Model model) {
		// 一定要帶一個空 form，否則 th:object="${form}" 會 NPE
		model.addAttribute("form", new ExhibitorRegisterForm());
		return "front-end/exhibitor_register";
	}
	
	@PostMapping("/exhibitor_register")
	public String register(
			@Valid @ModelAttribute("form") ExhibitorRegisterForm form,
			BindingResult bindingResult,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			// ⚠️ 一定要回到同一個 view 名稱
			return "front-end/exhibitor_register";
		}
		
		// duplicate
		// 主要給 國際化 (i18n) / message.properties 用的錯誤代碼
		// 如果你沒有 message.properties 對應，Spring 就會 fallback 用第三個參數。
		// 如果你不需要 i18n，可以直接寫死在這裡。
		if(EXHIBITOR_SVC.existsByBusinessIdNumber(form.getBusinessIdNumber())) {
			bindingResult.rejectValue("businessIdNumber", "duplicate", "統編已被註冊");
		}
		if(EXHIBITOR_SVC.existsByEmail(form.getEmail())) {
			bindingResult.rejectValue("email", "duplicate", "信箱已被註冊");
		}
		
	    // ⚠️ 確認密碼相同
	    if (!form.getPassword().equals(form.getConfirmPassword())) {
	        bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "兩次輸入的密碼不一致");
	        return "front-end/exhibitor_register";
	    }
		
		else System.out.println("ExhibitorRegisterController: 展商註冊成功！");
		
	    EXHIBITOR_SVC.exhibitorRegister(form);
	    
		return "redirect:/front-end/index?exhibitorRegister=true"; // 建議用 redirect，避免重送表單
	}
	
}
