package com.eventra.member.model;

import java.util.List;
import java.util.Optional;

public interface MemberDAO_Interface {

    // C
    Integer save(MemberVO vo);  // 回傳自增主鍵
    
    // R
    Optional<MemberVO> findById(Integer memberId);
    Optional<MemberVO> findByEmail(String email);
    Optional<MemberVO> findByNickname(String nickname);

    // U
    void update(MemberVO vo);

    // D
    void delete(Integer memberId);



    // 其他查詢/工具
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    // 登入（比對 email + password_hash）
    Optional<MemberVO> login(String email, String passwordHash);

    // 列表/分頁
    List<MemberVO> findAll();
    List<MemberVO> findPage(int page, int size); // page 從 1 開始
}
