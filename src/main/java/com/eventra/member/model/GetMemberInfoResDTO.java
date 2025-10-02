package com.eventra.member.model;

import java.time.LocalDate;

public class GetMemberInfoResDTO {
	private String email;
	private String fullName;
	private String nickname;
	private String gender;
	private String phoneNumber;
	private LocalDate birthDate;
	private String address;
	private String profilePic;
	private String lineUserId;
	private String githubId;
	private String googleId;
	private String facebookId;
	
	
	public String getLineUserId() {
		return lineUserId;
	}
	public void setLineUserId(String lineUserId) {
		this.lineUserId = lineUserId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public LocalDate getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getGithubId() {
		return githubId;
	}
	public void setGithubId(String githubId) {
		this.githubId = githubId;
	}
	public String getGoogleId() {
		return googleId;
	}
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}
	public String getFacebookId() {
		return facebookId;
	}
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	public String getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	// --- Builder（用 setter 寫入再回傳） ---
    public static class Builder {
        private String email;
        private String fullName;
        private String nickname;
        private String gender;
        private String phoneNumber;
        private LocalDate birthDate;
        private String address;
        private String profilePic;
        private String lineUserId;
        private String githubId;
        private String googleId;
        private String facebookId;

        public Builder email(String email) { this.email = email; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder nickname(String nickname) { this.nickname = nickname; return this; }
        public Builder gender(String gender) { this.gender = gender; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder birthDate(LocalDate birthDate) { this.birthDate = birthDate; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder profilePic(String profilePic) { this.profilePic = profilePic; return this; }
        public Builder lineUserId(String lineUserId) { this.lineUserId = lineUserId; return this; }
        public Builder githubId(String githubId) { this.githubId = githubId; return this; }
        public Builder googleId(String googleId) { this.googleId = googleId; return this; }
        public Builder facebookId(String facebookId) { this.facebookId = facebookId; return this; }

        public GetMemberInfoResDTO build() {
            GetMemberInfoResDTO dto = new GetMemberInfoResDTO(); // 不用帶參數建構子
            dto.setEmail(email);
            dto.setFullName(fullName);
            dto.setNickname(nickname);
            dto.setGender(gender);
            dto.setPhoneNumber(phoneNumber);
            dto.setBirthDate(birthDate);
            dto.setAddress(address);
            dto.setProfilePic(profilePic);
            dto.setLineUserId(lineUserId);
            dto.setGithubId(githubId);
            dto.setGoogleId(googleId);
            dto.setFacebookId(facebookId);
            return dto;
        }
    }
	
}
