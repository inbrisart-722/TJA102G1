package com.eventra.member.model;

import java.util.List;

/**
 * MemberService_Interface
 *
 * 單一職責（SRP）：定義會員模組的商業操作介面，不負責持久化細節與傳輸層控制。
 * 交易標註（@Transactional）由實作類別負責；此處只定義合約。
 *
 * 與 0205 專案參考 / ver3 資料表對齊：
 * - email 與 nickname 具唯一性（最終由 DB 約束保證；Service 做預檢與友善錯誤轉譯）
 * - passwordHash 於上層（Controller）已完成雜湊後再傳入
 */
public interface MemberService_Interface {

    // ===== 建立 / 更新 / 刪除 =====

    /**
     * 註冊新會員。
     * 前置條件：email、passwordHash、fullName、nickname 皆不可為空；email/nickname 未被使用。
     * 後置條件：回傳新建的 memberId。
     */
    Integer register(MemberVO member);

    /**
     * 更新會員一般資料（不含 email 與 passwordHash）。
     * 可更新欄位：fullName、gender、nickname、birthDate、phoneNumber、address、profilePic。
     */
    void updateProfile(MemberVO member);

    /**
     * 修改密碼雜湊（passwordHash）。
     * 前置條件：newPasswordHash 不可為空，上層已完成雜湊與強度檢核。
     */
    void changePassword(Integer memberId, String newPasswordHash);

    /**
     * 依主鍵刪除會員。
     */
    void remove(Integer memberId);

    // ===== 查詢 =====

    /**
     * 依主鍵取得會員。
     */
    MemberVO get(Integer memberId);

    /**
     * 取得所有會員（注意：大量資料時請改用分頁版本於實作層提供）。
     */
    List<MemberVO> getAll();

    /**
     * 以 email 取得會員（email 正規化：trim + toLowerCase 於實作處理）。
     */
    MemberVO getByEmail(String email);

    /**
     * 以 nickname 取得會員（trim 於實作處理）。
     */
    MemberVO getByNickname(String nickname);

    // ===== 可用性檢查 =====

    /**
     * 暱稱是否可用（大小寫敏感與否依 DB collation；此為預檢，最終以 DB 唯一鍵為準）。
     */
    boolean isNicknameAvailable(String nickname);

    /**
     * Email 是否可用（此為預檢，最終以 DB 唯一鍵為準）。
     */
    boolean isEmailAvailable(String email);
}
