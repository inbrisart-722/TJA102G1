package com.eventra.member.model;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class MemberVO {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "member_id")
    private Integer memberId;
	@Column(name = "email")
    private String email;
	@Column(name = "password_hash")
    private String passwordHash;
	@Column(name = "full_name")
    private String fullName;
    @Column(name = "gender", columnDefinition = "char(10)")
    private String gender;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "address")
    private String address;
    @Column(name = "profile_pic")
    private String profilePic;
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    
    /* OAuth2/OIDC 專用欄位 */
    
    @Column(name = "line_user_id")
    private String lineUserId;
    
    @Column(name = "google_id")
    private String googleId;
    @Column(name = "github_id")
    private String githubId;
    @Column(name = "facebook_id")
    private String facebookId;
    @Column(name = "asid")
    private String asid;
    
    /* getter | setter */
    
    
    public String getLineUserId() {
    	return lineUserId;
    }
    public void setLineUserId(String lineUserId) {
    	this.lineUserId = lineUserId;
    }
    public String getAsid() {
		return asid;
	}
	public void setAsid(String asid) {
		this.asid = asid;
	}
	public String getFacebookId() {
		return facebookId;
	}
	public void setFacebookId(String facebookId) {
    	this.facebookId = facebookId;
    }
    public String getGoogleId() {
    	return googleId;
    }
	public void setGoogleId(String googleId) {
    	this.googleId = googleId;
    }
    public String getGithubId() {
    	return githubId;
    }
    public void setGithubId(String githubId) {
    	this.githubId = githubId;
    }
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public LocalDate getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public static class Builder {
        private final String email;
        private final String passwordHash;
        private final String nickname;

        public Builder(String email, String passwordHash, String nickname) {
            this.email = email;
            this.passwordHash = passwordHash;
            this.nickname = nickname;
        }

        public MemberVO build() {
            MemberVO vo = new MemberVO();
            vo.setEmail(this.email);
            vo.setPasswordHash(this.passwordHash);
            vo.setNickname(this.nickname);
            return vo;
        }
    }

}
