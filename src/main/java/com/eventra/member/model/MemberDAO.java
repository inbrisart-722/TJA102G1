package com.eventra.member.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * MemberDAO
 * 職責：僅負責 MemberVO 的持久化 CRUD 與基礎查詢。
 * 注意：
 *  - 不做 email 正規化、暱稱唯一性檢核等商業邏輯（交給 Service）
 *  - 不吞例外；讓 Spring 進行例外轉換（@Repository）
 */
@Repository
public class MemberDAO implements MemberDAO_Interface {

    @PersistenceContext
    private EntityManager em;

    // JPQL 常數集中管理
    private static final String JPQL_FIND_ALL =
            "SELECT m FROM MemberVO m ORDER BY m.memberId";
    private static final String JPQL_FIND_BY_EMAIL =
            "SELECT m FROM MemberVO m WHERE m.email = :email";
    private static final String JPQL_FIND_BY_NICKNAME =
            "SELECT m FROM MemberVO m WHERE m.nickname = :nickname";

    // ====== C ======
    @Override
    @Transactional
    public void insert(MemberVO member) {
        em.persist(member);
        // Persist 後，IDENTITY 主鍵會回填到 entity
    }

    // ====== U ======
    @Override
    @Transactional
    public void update(MemberVO member) {
        em.merge(member);
    }

    // ====== D ======
    @Override
    @Transactional
    public void delete(Integer memberId) {
        if (memberId == null) return;
        MemberVO found = em.find(MemberVO.class, memberId);
        if (found != null) {
            em.remove(found);
        }
    }

    // ====== R ======
    @Override
    @Transactional(readOnly = true)
    public MemberVO findByPrimaryKey(Integer memberId) {
        if (memberId == null) return null;
        return em.find(MemberVO.class, memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberVO> getAll() {
        TypedQuery<MemberVO> q = em.createQuery(JPQL_FIND_ALL, MemberVO.class);
        return q.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberVO findByEmail(String email) {
        if (email == null) return null;
        TypedQuery<MemberVO> q = em.createQuery(JPQL_FIND_BY_EMAIL, MemberVO.class)
                                   .setParameter("email", email)
                                   .setMaxResults(1);
        List<MemberVO> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberVO findByNickname(String nickname) {
        if (nickname == null) return null;
        TypedQuery<MemberVO> q = em.createQuery(JPQL_FIND_BY_NICKNAME, MemberVO.class)
                                   .setParameter("nickname", nickname)
                                   .setMaxResults(1);
        List<MemberVO> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }
}
