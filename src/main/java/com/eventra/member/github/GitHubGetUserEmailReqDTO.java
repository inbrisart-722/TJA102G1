package com.eventra.member.github;

public class GitHubGetUserEmailReqDTO {
	private String email;
	// 使用者的 email
	private Boolean primary;
	// 是否為主要 email（true = 主帳號 email）
	private Boolean verified; 
	// 是否已驗證過（true = GitHub 驗證過此 email）
		// true 才能確保這是有效信箱（要用這個判斷）
	private String visibility;
	// 公開性 (public/null)，null = 私人
		// "public" → 使用者公開顯示
		// null → 隱私（只有你透過 OAuth scope 才能拿到）
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Boolean getPrimary() {
		return primary;
	}
	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}
	public Boolean getVerified() {
		return verified;
	}
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
}
