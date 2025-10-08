package com.eventra.exhibitor.model;

import com.eventra.exhibitor.model.ExhibitorRepository;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.exhibitor.ExhibitorResetTokenUtil;
import io.jsonwebtoken.JwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExhibitorPasswordResetService {

  private final ExhibitorRepository exhibitorRepository;
  private final PasswordEncoder passwordEncoder;
  private final JavaMailSender mailSender;
  private final ExhibitorResetTokenUtil tokenUtil;
  
  @Value("${spring.mail.username}")           // ← 從設定檔取寄件者帳號
  private String fromAddress;

  public ExhibitorPasswordResetService(
      ExhibitorRepository exhibitorRepository,
      PasswordEncoder passwordEncoder,
      JavaMailSender mailSender
  ) {
    this.exhibitorRepository = exhibitorRepository;
    this.passwordEncoder = passwordEncoder;
    this.mailSender = mailSender;
    this.tokenUtil = new ExhibitorResetTokenUtil(
        System.getenv().getOrDefault("APP_SECRET", "change-me-please"),
        1800 // 30 分鐘
    );
  }

  /** 1) 請求重設：發送信件（無論是否存在此 Email 都回 200，避免探測） */
  public void sendResetLink(String email, String siteBaseUrl) {
    var exOpt = exhibitorRepository.findByEmailIgnoreCase(email);
    if (exOpt.isEmpty()) {
      // 不洩漏資訊，但仍可在後台 log
      return;
    }
    ExhibitorVO ex = exOpt.get();
    String token = tokenUtil.create(ex.getExhibitorId(), ex.getEmail(), ex.getPasswordHash());
    String url = siteBaseUrl + "/back-end/exhibitor/reset_password?token=" + token + "&email=" + ex.getEmail();
    sendMail(ex.getEmail(), url);
  }

  /** 2) 送出新密碼：驗證 token -> 成功就更新密碼 */
  @Transactional
  public boolean resetPassword(String token, String email, String newPassword) {
    var exOpt = exhibitorRepository.findByEmailIgnoreCase(email);
    if (exOpt.isEmpty()) return false;

    var ex = exOpt.get();
    try {
      var jws = tokenUtil.parse(token, ex.getPasswordHash()); // 用「目前密碼」導出的 key 驗
      String sub = jws.getBody().getSubject();
      if (!sub.equals(String.valueOf(ex.getExhibitorId()))) return false;

      ex.setPasswordHash(passwordEncoder.encode(newPassword));
      exhibitorRepository.save(ex);
      return true;
    } catch (JwtException e) {
      // 可能過期、被竄改、簽章不符
      return false;
    }
  }

  private void sendMail(String to, String url) {
    var msg = new SimpleMailMessage();
    msg.setFrom(fromAddress);
    msg.setTo(to);
    msg.setSubject("【eventra 展商】重設密碼連結（30 分鐘內有效）");
    msg.setText("""
        您好，請在 30 分鐘內點擊以下連結重設密碼：
        %s

        若非您本人操作，請忽略本信。
        """.formatted(url));
    mailSender.send(msg);
  }
}
