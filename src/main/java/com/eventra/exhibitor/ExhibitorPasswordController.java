package com.eventra.exhibitor;

import com.eventra.exhibitor.backend.controller.dto.ForgotPasswordDTO;
import com.eventra.exhibitor.backend.controller.dto.ResetPasswordDTO;
import com.eventra.exhibitor.model.ExhibitorPasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/back-end/exhibitor")
public class ExhibitorPasswordController {

	private final ExhibitorPasswordResetService resetService;

	public ExhibitorPasswordController(ExhibitorPasswordResetService resetService) {
		this.resetService = resetService;
	}

	/** 忘記密碼頁（輸入 email） **/
	@GetMapping("/forgot_password")
	public String forgotPage(Model model) {
		if (!model.containsAttribute("form")) {
			model.addAttribute("form", new ForgotPasswordDTO());
		}
		return "back-end/forgot_password";
	}

	/** 提交 email，寄出連結 **/
	@PostMapping("/forgot_password")
	public String sendLink(@Valid @ModelAttribute("form") ForgotPasswordDTO form, BindingResult br,
			HttpServletRequest req, RedirectAttributes ra) {

		if (br.hasErrors()) {
			// 帶回驗證錯誤（Flash Attributes）
			ra.addFlashAttribute("org.springframework.validation.BindingResult.form", br);
			ra.addFlashAttribute("form", form);
			return "redirect:/back-end/exhibitor/forgot_password";
		}
		String base = req.getRequestURL().toString().replace(req.getRequestURI(), "");
		resetService.sendResetLink(form.getEmail(), base);
		ra.addFlashAttribute("msg", "若 Email 存在，已寄出重設連結（30 分內有效）");
		return "redirect:/back-end/exhibitor/forgot_password";
	}

	/** 重設頁（從信中的連結進來） **/
	@GetMapping("/reset_password")
	public String resetPage(@RequestParam String token, @RequestParam String email, Model model) {

		ResetPasswordDTO form = new ResetPasswordDTO();
		form.setToken(token);
		form.setEmail(email);
		model.addAttribute("form", form);
		return "back-end/reset_password";
	}

	/** 提交新密碼 **/
	@PostMapping("/reset_password")
	public String doReset(@Valid @ModelAttribute("form") ResetPasswordDTO form, BindingResult br,
			RedirectAttributes ra) {

		if (br.hasErrors()) {
			// 有欄位錯誤 → 回到同頁顯示
			return "back-end/reset_password";
		}

		boolean ok = resetService.resetPassword(form.getToken(), form.getEmail(), form.getPassword());
		if (!ok) {
			// token 無效/過期或 email 不存在
			br.reject("token.invalid", "連結無效或已逾時，請重新申請重設密碼");
			return "back-end/reset_password";
		}
		// 成功：帶一段 flash 訊息，並 redirect 到登入頁
		ra.addFlashAttribute("msg", "密碼已更新，請以新密碼登入");
		return "redirect:/back-end/exhibitor/exhibitor_login";
	}
}
