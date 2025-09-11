package com.eventra.member.controller;

import com.eventra.member.model.MemberService_Interface;
import com.eventra.member.model.MemberVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MemberController
 *
 * 設計原則（SOLID / 0205專案風格）：
 * - 單一職責：僅處理 HTTP I/O 與 DTO 映射；不含商業規則與資料存取（交由 Service/DAO）。
 * - 清楚依賴：依賴抽象的 MemberService_Interface，利於替換與測試。
 * - 穩健 I/O：使用 @Valid/@Validated 進行輸入檢核；輸出固定以 DTO 回傳，避免 Entity 外洩。
 * - RESTful：建立回 201 + Location、更新/刪除回 204、找不到回 404、衝突回 409。
 *
 * 資料結構與欄位對齊 ver3（實際約束由 DB 與 Service 確保）。
 */
@RestController
@RequestMapping("/api/members")
@Validated
public class MemberController {

    private final MemberService_Interface service;

    public MemberController(MemberService_Interface service) {
        this.service = service;
    }

    // ============================ Create ============================

    /**
     * 註冊新會員（passwordHash 應為上層已完成雜湊的字串）
     */
    @PostMapping
    public ResponseEntity<IdResponse> register(@RequestBody @Valid RegisterRequest req) {
        MemberVO vo = toEntity(req);
        Integer id = service.register(vo);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/members/" + id));
        return new ResponseEntity<>(new IdResponse(id), headers, HttpStatus.CREATED);
    }

    // ============================ Read ============================

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> get(@PathVariable("id") @NotNull @Positive Integer id) {
        MemberVO vo = service.get(id);
        if (vo == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(toResponse(vo));
    }

    /**
     * 依條件查詢：
     * - ?email=xxx 以 email 精準查
     * - ?nickname=xxx 以 nickname 精準查
     * - 未帶條件：回傳全列表（大量資料請在 Service/DAO 另做分頁介面）
     */
    @GetMapping
    public ResponseEntity<List<MemberResponse>> query(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "nickname", required = false) String nickname) {

        if (email != null && !email.isBlank()) {
            MemberVO one = service.getByEmail(email);
            return ResponseEntity.ok(one == null ? List.of() : List.of(toResponse(one)));
        }
        if (nickname != null && !nickname.isBlank()) {
            MemberVO one = service.getByNickname(nickname);
            return ResponseEntity.ok(one == null ? List.of() : List.of(toResponse(one)));
        }

        List<MemberResponse> list = service.getAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /**
     * 可用性檢查（預檢；最終仍以 DB 唯一鍵為準）
     * 例：/api/members/availability?email=foo@bar.com 或 ?nickname=Alice
     */
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> availability(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "nickname", required = false) String nickname) {

        Boolean emailOk = null, nicknameOk = null;
        if (email != null) emailOk = service.isEmailAvailable(email);
        if (nickname != null) nicknameOk = service.isNicknameAvailable(nickname);
        return ResponseEntity.ok(new AvailabilityResponse(emailOk, nicknameOk));
    }

    // ============================ Update ============================

    /**
     * 更新一般資料（不含 email/passwordHash）
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProfile(@PathVariable("id") @NotNull @Positive Integer id,
                                              @RequestBody @Valid UpdateProfileRequest req) {
        MemberVO src = toEntity(id, req);
        service.updateProfile(src);
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改密碼（僅接受雜湊字串）
     */
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable("id") @NotNull @Positive Integer id,
                                               @RequestBody @Valid PasswordChangeRequest req) {
        service.changePassword(id, req.getNewPasswordHash());
        return ResponseEntity.noContent().build();
    }

    // ============================ Delete ============================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable("id") @NotNull @Positive Integer id) {
        service.remove(id);
        return ResponseEntity.noContent().build();
    }

    // ============================ DTO / Mapper ============================

    private MemberVO toEntity(RegisterRequest r) {
        MemberVO vo = new MemberVO();
        vo.setEmail(r.getEmail());
        vo.setPasswordHash(r.getPasswordHash());
        vo.setFullName(r.getFullName());
        vo.setGender(r.getGender());
        vo.setNickname(r.getNickname());
        vo.setBirthDate(r.getBirthDate());
        vo.setPhoneNumber(r.getPhoneNumber());
        vo.setAddress(r.getAddress());
        vo.setProfilePic(r.getProfilePic());
        return vo;
    }

    private MemberVO toEntity(Integer id, UpdateProfileRequest r) {
        MemberVO vo = new MemberVO();
        vo.setMemberId(id);
        vo.setFullName(r.getFullName());
        vo.setGender(r.getGender());
        vo.setNickname(r.getNickname());
        vo.setBirthDate(r.getBirthDate());
        vo.setPhoneNumber(r.getPhoneNumber());
        vo.setAddress(r.getAddress());
        vo.setProfilePic(r.getProfilePic());
        return vo;
    }

    private MemberResponse toResponse(MemberVO m) {
        return new MemberResponse(
                m.getMemberId(),
                m.getEmail(),
                m.getFullName(),
                m.getGender(),
                m.getNickname(),
                m.getBirthDate(),
                m.getPhoneNumber(),
                m.getAddress(),
                m.getCreatedAt(),
                m.getUpdatedAt(),
                m.getProfilePic() != null
        );
    }

    // ---------- DTOs ----------

    public static final class IdResponse {
        private final Integer id;
        public IdResponse(Integer id) { this.id = id; }
        public Integer getId() { return id; }
    }

    public static final class AvailabilityResponse {
        private final Boolean emailAvailable;     // 可能為 null（未查）
        private final Boolean nicknameAvailable;  // 可能為 null（未查）
        public AvailabilityResponse(Boolean emailAvailable, Boolean nicknameAvailable) {
            this.emailAvailable = emailAvailable;
            this.nicknameAvailable = nicknameAvailable;
        }
        public Boolean getEmailAvailable() { return emailAvailable; }
        public Boolean getNicknameAvailable() { return nicknameAvailable; }
    }

    public static final class MemberResponse {
        private final Integer memberId;
        private final String email;
        private final String fullName;
        private final String gender;
        private final String nickname;
        private final LocalDate birthDate;
        private final String phoneNumber;
        private final String address;
        private final Timestamp createdAt;
        private final Timestamp updatedAt;
        private final boolean hasProfilePic;

        public MemberResponse(Integer memberId, String email, String fullName, String gender,
                              String nickname, LocalDate birthDate, String phoneNumber,
                              String address, Timestamp createdAt, Timestamp updatedAt,
                              boolean hasProfilePic) {
            this.memberId = memberId;
            this.email = email;
            this.fullName = fullName;
            this.gender = gender;
            this.nickname = nickname;
            this.birthDate = birthDate;
            this.phoneNumber = phoneNumber;
            this.address = address;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.hasProfilePic = hasProfilePic;
        }
        public Integer getMemberId() { return memberId; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getGender() { return gender; }
        public String getNickname() { return nickname; }
        public LocalDate getBirthDate() { return birthDate; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getAddress() { return address; }
        public Timestamp getCreatedAt() { return createdAt; }
        public Timestamp getUpdatedAt() { return updatedAt; }
        public boolean isHasProfilePic() { return hasProfilePic; }
    }

    public static final class RegisterRequest {
        @NotBlank @Email @Size(max = 254)
        private String email;

        @NotBlank @Size(max = 128)
        private String passwordHash; // 已雜湊

        @NotBlank @Size(max = 50)
        private String fullName;

        @Size(max = 1)
        private String gender; // 'M'/'F'/'U'...

        @NotBlank @Size(max = 50)
        private String nickname;

        private LocalDate birthDate;

        @Size(max = 15)
        private String phoneNumber;

        @Size(max = 255)
        private String address;

        private byte[] profilePic;

        public RegisterRequest() {}
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public byte[] getProfilePic() { return profilePic; }
        public void setProfilePic(byte[] profilePic) { this.profilePic = profilePic; }
    }

    public static final class UpdateProfileRequest {
        @Size(max = 50)
        private String fullName;

        @Size(max = 1)
        private String gender;

        @Size(max = 50)
        private String nickname;

        private LocalDate birthDate;

        @Size(max = 15)
        private String phoneNumber;

        @Size(max = 255)
        private String address;

        private byte[] profilePic;

        public UpdateProfileRequest() {}
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public byte[] getProfilePic() { return profilePic; }
        public void setProfilePic(byte[] profilePic) { this.profilePic = profilePic; }
    }

    public static final class PasswordChangeRequest {
        @NotBlank @Size(max = 128)
        private String newPasswordHash; // 已雜湊
        public PasswordChangeRequest() {}
        public String getNewPasswordHash() { return newPasswordHash; }
        public void setNewPasswordHash(String newPasswordHash) { this.newPasswordHash = newPasswordHash; }
    }

    // ============================ Exception Mapping ============================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }
}
