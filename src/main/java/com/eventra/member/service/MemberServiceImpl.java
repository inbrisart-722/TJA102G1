package com.eventra.member.service;

import java.util.Locale;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.member.model.MemberRepository;
import com.eventra.member.model.MemberVO;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repo;
    private final PasswordEncoder encoder;

    public MemberServiceImpl(MemberRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    // -------------------- Public APIs --------------------

    @Transactional
    @Override
    public MemberVO register(String email, String rawPassword, String nickname, String fullName) {
        // 基本檢查（Service 層仍要自保，不能只靠 Controller 的 @Valid）
        assertNotBlank(email, "Email 不可為空");
        assertNotBlank(rawPassword, "密碼不可為空");
        assertNotBlank(nickname, "暱稱不可為空");

        final String normalizedEmail = normalizeEmail(email);
        final String trimmedNickname = nickname.trim(); // 大小寫敏感 → 不轉大小寫

        if (repo.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email 已被註冊");
        }
        if (repo.existsByNickname(trimmedNickname)) {
            throw new IllegalArgumentException("暱稱已被使用（大小寫敏感）");
        }

        MemberVO m = new MemberVO();
        m.setEmail(normalizedEmail);
        m.setPasswordHash(encoder.encode(rawPassword));
        m.setNickname(trimmedNickname);
        m.setFullName(blankToNull(fullName));

        return repo.save(m);
    }

    @Transactional(readOnly = true)
    @Override
    public MemberVO loginWithEmailOrNickname(String identifier, String rawPassword) {
        assertNotBlank(identifier, "帳號不可為空");
        assertNotBlank(rawPassword, "密碼不可為空");

        MemberVO m;
        if (identifier.contains("@")) {
            m = repo.findByEmail(normalizeEmail(identifier))
                    .orElseThrow(() -> new IllegalArgumentException("帳號不存在"));
        } else {
            // 暱稱大小寫敏感：不轉大小寫，只去空白
            m = repo.findByNickname(identifier.trim())
                    .orElseThrow(() -> new IllegalArgumentException("帳號不存在"));
        }

        if (!encoder.matches(rawPassword, m.getPasswordHash())) {
            throw new IllegalArgumentException("密碼錯誤");
        }
        return m;
        // 若要回傳 Token，請在 Controller 層組合 Token 與對外 DTO
    }

    @Transactional(readOnly = true)
    @Override
    public MemberVO getById(Integer id) {
        Objects.requireNonNull(id, "id 不可為 null");
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("會員不存在"));
    }

    @Transactional
    @Override
    public MemberVO updateProfile(Integer id, String fullName, String phoneNumber, String address, byte[] profilePic) {
        MemberVO m = getById(id);

        if (fullName != null)    m.setFullName(blankToNull(fullName));
        if (phoneNumber != null) m.setPhoneNumber(blankToNull(phoneNumber));
        if (address != null)     m.setAddress(blankToNull(address));
        if (profilePic != null)  m.setProfilePic(profilePic);

        return repo.save(m);
    }

    @Transactional
    @Override
    public void changePassword(Integer id, String currentPassword, String newPassword) {
        assertNotBlank(currentPassword, "目前密碼不可為空");
        assertNotBlank(newPassword, "新密碼不可為空");
        if (newPassword.length() < 8 || newPassword.length() > 72) {
            throw new IllegalArgumentException("新密碼長度需在 8~72 字元之間");
        }

        MemberVO m = getById(id);
        if (!encoder.matches(currentPassword, m.getPasswordHash())) {
            throw new IllegalArgumentException("目前密碼不正確");
        }
        if (encoder.matches(newPassword, m.getPasswordHash())) {
            // 與現有雜湊比對成功代表新舊相同
            throw new IllegalArgumentException("新密碼不可與目前密碼相同");
        }

        m.setPasswordHash(encoder.encode(newPassword));
        repo.save(m);
    }

    // -------------------- Helpers --------------------

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String blankToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private static void assertNotBlank(String s, String message) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
