package com.eventra.exhibitor;

import com.eventra.exhibitor.model.ExhibitorPasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/back-end/exhibitor")
public class ExhibitorPasswordController {

  private final ExhibitorPasswordResetService resetService;

  public ExhibitorPasswordController(ExhibitorPasswordResetService resetService) {
    this.resetService = resetService;
  }

  /** [GET] 忘記密碼頁（輸入 email） */
  @GetMapping("/forgot_password")
  public String forgotPage() {
    return "back-end/forgot_password";
  }

  /** [POST] 提交 email，寄出連結 */
  @PostMapping("/forgot_password")
  public String sendLink(@RequestParam String email, HttpServletRequest req, Model model) {
    String base = req.getRequestURL().toString().replace(req.getRequestURI(), "");
    resetService.sendResetLink(email, base);
    model.addAttribute("msg", "若 Email 存在，已寄出重設連結（30 分內有效）");
    return "back-end/forgot_password";
  }

  /** [GET] 重設頁（從信中的連結進來） */
  @GetMapping("/reset_password")
  public String resetPage(@RequestParam String token, @RequestParam String email, Model model) {
    model.addAttribute("token", token);
    model.addAttribute("email", email);
    return "back-end/reset_password";
  }

  /** [POST] 提交新密碼 */
  @PostMapping("/reset_password")
  public String doReset(@RequestParam String token,
                        @RequestParam String email,
                        @RequestParam String password,
                        Model model) {
    boolean ok = resetService.resetPassword(token, email, password);
    model.addAttribute("ok", ok);
    model.addAttribute("msg", ok ? "密碼已更新，請以新密碼登入" : "連結無效或已逾時");
    return "back-end/reset_password_result";
  }
}
