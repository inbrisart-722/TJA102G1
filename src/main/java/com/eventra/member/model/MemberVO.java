package com.eventra.member.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * MemberVO (JPA Entity) SRP：僅負責持久化結構與欄位約束，不摻雜商業邏輯。 對齊 0205 專案參考與
 * ver3：email/nickname 唯一、password_hash 使用 ASCII + ascii_bin。
 */
@Entity // 要加上@Entity才能成為JPA的一個Entity類別
@Access(AccessType.FIELD)

//代表這個class是對應到資料庫的實體table，目前對應的table是member 
@Table(name = "member", uniqueConstraints = { @UniqueConstraint(name = "uk_member_email", columnNames = "email"),
		@UniqueConstraint(name = "uk_member_nickname", columnNames = "nickname") }, indexes = {
				@Index(name = "idx_member_email", columnList = "email"),
				@Index(name = "idx_member_nickname", columnList = "nickname") })
public class MemberVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer memberId;
	private String email;
	private String passwordHash;
	private String fullName;
	private String gender; // 'M' / 'F' / 'U'...
	private String nickname;
	private LocalDate birthDate;
	private String phoneNumber;
	private String address;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private byte[] profilePic;

	// ===== 主鍵 =====
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id", updatable = false, nullable = false)

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	// ===== 帳號資料 =====
	@NotBlank
	@Email
	@Size(max = 254)
	@Column(name = "email", nullable = false, length = 254, columnDefinition = "varchar(254) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotBlank
	@Size(max = 128)
	@Column(name = "password_hash", nullable = false, length = 128, columnDefinition = "varchar(128) CHARACTER SET ascii COLLATE ascii_bin")
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	// ===== 個人資訊 =====
	@NotBlank
	@Size(max = 50)
	@Column(name = "full_name", nullable = false, length = 50, columnDefinition = "varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Size(max = 1)
	@Column(name = "gender", length = 1)
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@NotBlank
	@Size(max = 50)
	@Column(name = "nickname", nullable = false, length = 50,
			// 以 bin collation 確保大小寫敏感唯一；若 ver3 指定其他 collation，請依 ver3 調整
			columnDefinition = "varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin")
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Column(name = "birth_date")
	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	@Size(max = 15)
	@Column(name = "phone_number", length = 15)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Size(max = 255)
	@Column(name = "address", length = 255, columnDefinition = "varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	// ===== 系統欄位 =====
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP")
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false, columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	// ===== BLOB =====
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "profile_pic")

	public byte[] getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}

	// ===== 建構式（符合 JPA 要求） =====
	public MemberVO() {
	}

	public MemberVO(Integer memberId) {
		this.memberId = memberId;
	}

	// ===== 等值與雜湊：已持久化用主鍵；未持久化退回 email（自然鍵）=====
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof MemberVO))
			return false;
		MemberVO other = (MemberVO) o;

		if (this.memberId != null && other.memberId != null) {
			return Objects.equals(this.memberId, other.memberId);
		}
		return Objects.equals(this.email, other.email);
	}

	@Override
	public int hashCode() {
		return (memberId != null) ? Objects.hash(memberId) : Objects.hash(email);
	}

	// 注意：不輸出 passwordHash 內容，避免敏感資訊外洩
	@Override
	public String toString() {
		return "MemberVO{" + "memberId=" + memberId + ", email='" + email + '\'' + ", fullName='" + fullName + '\''
				+ ", gender='" + gender + '\'' + ", nickname='" + nickname + '\'' + ", birthDate=" + birthDate
				+ ", phoneNumber='" + phoneNumber + '\'' + ", address='" + summarize(address) + '\'' + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + ", profilePic="
				+ (profilePic == null ? "null" : (profilePic.length + " bytes")) + '}';
	}

	private static String summarize(String s) {
		if (s == null)
			return null;
		return s.length() > 24 ? s.substring(0, 24) + "..." : s;
	}
}
