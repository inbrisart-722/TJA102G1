package com.eventra.exhibitor.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventra.exhibitor.model.ExhibitorRegisterForm;
import com.eventra.exhibitor.model.ExhibitorService;
import com.eventra.exhibitor.model.ExhibitorVO;

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
			@RequestParam(name = "token", required = false) String token,
			Model model) {
		
		model.addAttribute("token", token);
		
		/* ========== 1: 共用錯誤 ==========*/
		if (bindingResult.hasErrors()) {
			// ⚠️ 一定要回到同一個 view 名稱
			return "front-end/exhibitor_register";
		}
		
	    // ⚠️ 確認密碼相同
	    if (!form.getPassword().equals(form.getConfirmPassword())) {
	        bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "兩次輸入的密碼不一致");
			return "front-end/exhibitor_register";
	    }
	    /* ========== 2: token 區（展商審核未通過帶回來的）==========*/
		if(token != null && !token.isBlank() && EXHIBITOR_SVC.findByToken(token) != null) { // 多檢查一個如果 token 來亂就不理他，正常走
			ExhibitorVO exhibitor = EXHIBITOR_SVC.findByToken(token);
			
			// 統編：加上條件，跟展商原本的相同就不檢查唯一
			String id = form.getBusinessIdNumber();
			if(EXHIBITOR_SVC.existsByBusinessIdNumber(id) && !exhibitor.getBusinessIdNumber().equals(id)) {
				bindingResult.rejectValue("businessIdNumber", "duplicate", "統編已被註冊");
				return "front-end/exhibitor_register";
			}
			
			// 信箱：加上條件，跟展商原本的相同就不檢查唯一
			String email = form.getEmail();
			if(EXHIBITOR_SVC.existsByEmail(email) && !exhibitor.getEmail().equals(email)) {
				bindingResult.rejectValue("email", "duplicate", "信箱已被註冊");
				return "front-end/exhibitor_register";
			}
			EXHIBITOR_SVC.exhibitorRegisterWithToken(form, token);
		}
		
		/* ========== 3: non-token 區 ==========*/
		else {
			// duplicate
			// 主要給 國際化 (i18n) / message.properties 用的錯誤代碼
			// 如果你沒有 message.properties 對應，Spring 就會 fallback 用第三個參數。
			// 如果你不需要 i18n，可以直接寫死在這裡。
			if(EXHIBITOR_SVC.existsByBusinessIdNumber(form.getBusinessIdNumber())) {
				bindingResult.rejectValue("businessIdNumber", "duplicate", "統編已被註冊");
				return "front-end/exhibitor_register";
			}
			if(EXHIBITOR_SVC.existsByEmail(form.getEmail())) {
				bindingResult.rejectValue("email", "duplicate", "信箱已被註冊");
				return "front-end/exhibitor_register";
			}
			EXHIBITOR_SVC.exhibitorRegister(form);
		}
		return "redirect:/front-end/index?exhibitorRegister=true"; // 建議用 redirect，避免重送表單
	}
}
