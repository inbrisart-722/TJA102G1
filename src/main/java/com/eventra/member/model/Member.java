package com.eventra.member.model;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(
    name = "member",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_nickname", columnNames = "nickname")
    }
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    private Integer memberId;

    @Column(name = "email", nullable = false, length = 254, unique = true,
            columnDefinition = "varchar(254) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String email;

    @Column(name = "password_hash", nullable = false, length = 128,
            columnDefinition = "varchar(128) CHARACTER SET ascii COLLATE ascii_bin")
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 50,
            columnDefinition = "varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String fullName;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "nickname", nullable = false, length = 50, unique = true,
            columnDefinition = "varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nickname;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "address", length = 255,
            columnDefinition = "varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "timestamp default current_timestamp on update current_timestamp")
    private Timestamp updatedAt;

    @Lob
    @Column(name = "profile_pic")
    private byte[] profilePic;

    // ---- Constructors ----
    public Member() {}

    // ---- Getters & Setters ----
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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
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

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }
}
