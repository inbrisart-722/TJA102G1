package com.eventra.member.model;

import java.util.List;


public interface MemberDAO_Interface {

    // ====== C ======
    /** 新增一筆會員資料（成功後由 JPA 回填自增主鍵）。 */
    void insert(MemberVO member);

    // ====== R ======
    /** 依主鍵查詢會員。 */
    MemberVO findByPrimaryKey(Integer memberId);
    
    // ====== U ======
    /** 更新一筆會員資料（以主鍵對應目標）。 */
    void update(MemberVO member);

    // ====== D ======
    /** 依主鍵刪除一筆會員資料（若不存在則忽略）。 */
    void delete(Integer memberId);



    /** 查詢全部會員（大量資料時建議在實作層提供分頁版本）。 */
    List<MemberVO> getAll();

    /** 以 email 查詢會員（email 正規化由 Service 層處理）。 */
    MemberVO findByEmail(String email);

    /** 以 nickname 查詢會員（trim 由 Service 層處理）。 */
    MemberVO findByNickname(String nickname);
}
