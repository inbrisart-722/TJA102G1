package com.eventra.member.dto;

import jakarta.validation.constraints.*;

public class RegisterRequest {
    @Email @NotBlank @Size(max = 254)
    private String email;

    @NotBlank @Size(min = 8, max = 72)
    private String password;

    @NotBlank @Size(max = 50)
    private String nickname;

    @Size(max = 50)
    private String fullName;

    // getters/setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
