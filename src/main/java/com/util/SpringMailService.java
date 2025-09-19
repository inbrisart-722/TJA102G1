package com.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SpringMailService {
	
	// spring-boot-starter-mail 引入依賴
	
	private final JavaMailSender mailSender;
	
	public SpringMailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void sendSimpleMail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("brianchen8907222@gmail.com"); // 發件人
        message.setTo(to);                         // 收件人
        message.setSubject(subject);               // 標題
        message.setText(text);                     // 內容
        mailSender.send(message);
	}
	
	public void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException {
	    MimeMessage mimeMessage = mailSender.createMimeMessage();
	    // 原生 JavaMail 類別，直接用 MimeMessage 很麻煩，因為是低階 API，要自己組 MIME parts
	    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	    // MimeMessageHelper 就是 Spring 封裝的一個輔助類, 通常實務上 一定會搭配 MimeMessageHelper

	    helper.setFrom("brianchen8907222@gmail.com");
	    helper.setTo(to);
	    helper.setSubject(subject);
	    helper.setText(htmlContent, true); // true = HTML

	    mailSender.send(mimeMessage);
	}
}
