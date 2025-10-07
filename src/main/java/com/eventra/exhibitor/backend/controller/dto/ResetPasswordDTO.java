package com.eventra.exhibitor.backend.controller.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResetPasswordDTO {

	@NotBlank(message = "缺少驗證參數")
	private String token;

	@Email(message = "請輸入註冊Email")
	@NotBlank(message = "請輸入 Email")
	private String email;

	@NotBlank(message = "請輸入新密碼")
	@Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d).{8,128}", message = "密碼需至少 8 碼，且同時包含至少 1 個英文字母與 1 個數字")
	private String password;

	@NotBlank(message = "請再次輸入新密碼")
	private String confirmPassword;

	// 方便用 @AssertTrue 做跨欄位驗證（確認密碼一致）
	@AssertTrue(message = "兩次輸入的密碼不一致")
	public boolean isPasswordConfirmed() {
		if (password == null || confirmPassword == null)
			return false;
		return password.equals(confirmPassword);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
