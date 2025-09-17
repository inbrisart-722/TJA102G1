package com.eventra.member.service;

import com.eventra.member.model.MemberVO;

/**
 * 會員領域服務（與 Web 層無關，僅處理商業規則）。
 * 備註：
 * - 暱稱大小寫敏感唯一（DB 層使用 utf8mb4_bin 已保障）
 * - email 正規化為小寫 + 去除首尾空白後再檢查唯一
 * - 密碼存放一律為雜湊（BCrypt）
 */
public interface MemberService {

    /**
     * 註冊新會員。
     * @throws IllegalArgumentException Email 已被註冊／暱稱已被使用／參數不合法
     */
    MemberVO register(String email, String rawPassword, String nickname, String fullName);

    /**
     * 以 email 或 nickname 登入。
     * @param identifier email（不分大小寫）或 nickname（大小寫敏感）
     * @throws IllegalArgumentException 帳號不存在／密碼錯誤
     */
    MemberVO loginWithEmailOrNickname(String identifier, String rawPassword);

    /**
     * 以主鍵查詢（找不到即丟例外）。
     * @throws IllegalArgumentException 會員不存在
     */
    MemberVO getById(Integer id);

    /**
     * 局部更新個資（null 欄位忽略不更新）。
     * @return 更新後的 MemberVO
     * @throws IllegalArgumentException 會員不存在
     */
    MemberVO updateProfile(Integer id, String fullName, String phoneNumber, String address, byte[] profilePic);

    /**
     * 變更密碼（需提供目前密碼）。
     * @throws IllegalArgumentException 目前密碼不正確／會員不存在
     */
    void changePassword(Integer id, String currentPassword, String newPassword);
}
