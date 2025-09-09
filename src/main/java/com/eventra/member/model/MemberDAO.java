package com.eventra.member.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.eventra.member.controller.HibernateUtil;



public class MemberDAO implements MemberDAO_Interface {

    // -------- Create --------
    @Override
    public Integer save(MemberVO vo) {
        Transaction tx = null;
        try (Session session = com.eventra.member.controller.HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Integer id = (Integer) session.save(vo);
            tx.commit();
            return id;
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    // -------- Update --------
    @Override
    public void update(MemberVO vo) {
        Transaction tx = null;
        try (Session session = com.eventra.member.controller.HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(vo); // Hibernate 5/6：merge 可處理 detached entity
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    // -------- Delete --------
    @Override
    public void delete(Integer memberId) {
        Transaction tx = null;
        try (Session session = com.eventra.member.controller.HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            MemberVO found = session.get(MemberVO.class, memberId);
            if (found != null) {
                session.remove(found);
            }
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    // -------- Read --------
    @Override
    public Optional<MemberVO> findById(Integer memberId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            MemberVO vo = session.get(MemberVO.class, memberId);
            return Optional.ofNullable(vo);
        }
    }

    @Override
    public Optional<MemberVO> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MemberVO> q = session.createQuery(
                "from MemberVO where email = :email", MemberVO.class);
            q.setParameter("email", email);
            return q.uniqueResultOptional();
        }
    }

    @Override
    public Optional<MemberVO> findByNickname(String nickname) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MemberVO> q = session.createQuery(
                "from MemberVO where nickname = :nickname", MemberVO.class);
            q.setParameter("nickname", nickname);
            return q.uniqueResultOptional();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> q = session.createQuery(
                "select count(m) from MemberVO m where m.email = :email", Long.class);
            q.setParameter("email", email);
            return q.uniqueResult() > 0;
        }
    }

    @Override
    public boolean existsByNickname(String nickname) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> q = session.createQuery(
                "select count(m) from MemberVO m where m.nickname = :nickname", Long.class);
            q.setParameter("nickname", nickname);
            return q.uniqueResult() > 0;
        }
    }

    @Override
    public Optional<MemberVO> login(String email, String passwordHash) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MemberVO> q = session.createQuery(
                "from MemberVO where email = :email and passwordHash = :pwd", MemberVO.class);
            q.setParameter("email", email);
            q.setParameter("pwd", passwordHash);
            return q.uniqueResultOptional();
        }
    }

    @Override
    public List<MemberVO> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from MemberVO order by memberId", MemberVO.class)
                          .list();
        }
    }

    @Override
    public List<MemberVO> findPage(int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.max(size, 1);
        int offset = (p - 1) * s;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MemberVO> q = session.createQuery(
                "from MemberVO order by memberId", MemberVO.class);
            q.setFirstResult(offset);
            q.setMaxResults(s);
            return q.list();
        }
    }

    // -------- helper --------
    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            try { tx.rollback(); } catch (RuntimeException ignored) {}
        }
    }

	public List<MemberVO> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public MemberVO findByPrimaryKey(Integer memberId) {
		// TODO Auto-generated method stub
		return null;
	}
}
