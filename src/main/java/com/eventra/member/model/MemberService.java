package com.eventra.member.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;

/**
 * MemberService 職責：商業規則與流程協調（正規化、唯一性預檢、欄位更新範圍）。 注意：不做持久化細節（交由
 * MemberDAO），不做雜湊（上層先把明碼轉成 hash）。
 */
@Service
public class MemberService implements MemberService_Interface {

	private final MemberDAO_Interface dao;

	public MemberService(MemberDAO_Interface dao) {
		this.dao = dao;
	}

	// ==================== 基礎 CRUD ====================

	@Override
	@Transactional
	public Integer register(MemberVO memberVO) {
		Objects.requireNonNull(memberVO, "member 不可為 null");

		// ---- 正規化（屬 Service 範圍，不塞進 DAO/Entity）----
		memberVO.setEmail(normEmail(memberVO.getEmail()));
		memberVO.setNickname(safeTrim(memberVO.getNickname()));
		memberVO.setFullName(safeTrim(memberVO.getFullName()));
		memberVO.setPhoneNumber(safeTrim(memberVO.getPhoneNumber()));
		memberVO.setAddress(safeTrim(memberVO.getAddress()));

		// ---- 基本驗證（必要欄位）----
		requireNotBlank(memberVO.getEmail(), "email 不可為空");
		requireNotBlank(memberVO.getPasswordHash(), "passwordHash 不可為空");
		requireNotBlank(memberVO.getFullName(), "fullName 不可為空");
		requireNotBlank(memberVO.getNickname(), "nickname 不可為空");

		// ---- 唯一性預檢（提早回饋；最終仍以 DB 約束為準）----
		if (!isEmailAvailable(memberVO.getEmail())) {
			throw new IllegalArgumentException("Email 已被使用");
		}
		if (!isNicknameAvailable(memberVO.getNickname())) {
			throw new IllegalArgumentException("暱稱已被使用");
		}

		// ---- 寫入 ----
		try {
			dao.insert(memberVO); // 回填 IDENTITY 主鍵
		} catch (DataIntegrityViolationException ex) {
			// 兜底處理競態寫入造成的唯一鍵違反
			throw new IllegalStateException("建立會員失敗：違反唯一鍵（email 或 nickname）", ex);
		}
		return memberVO.getMemberId();
	}

	@Override
	@Transactional
	public void updateProfile(MemberVO src) {
		Objects.requireNonNull(src, "member 不可為 null");
		if (src.getMemberId() == null) {
			throw new IllegalArgumentException("更新需要 memberId");
		}

		MemberVO db = dao.findByPrimaryKey(src.getMemberId());
		if (db == null)
			throw new IllegalArgumentException("找不到該會員");

		// 限定可更新欄位（不含 email、passwordHash）
		if (src.getFullName() != null)
			db.setFullName(safeTrim(src.getFullName()));
		if (src.getGender() != null)
			db.setGender(src.getGender());
		if (src.getBirthDate() != null)
			db.setBirthDate(src.getBirthDate());
		if (src.getPhoneNumber() != null)
			db.setPhoneNumber(safeTrim(src.getPhoneNumber()));
		if (src.getAddress() != null)
			db.setAddress(safeTrim(src.getAddress()));
		if (src.getProfilePic() != null)
			db.setProfilePic(src.getProfilePic());

		if (src.getNickname() != null) {
			String newNick = safeTrim(src.getNickname());
			if (!newNick.equals(db.getNickname()) && !isNicknameAvailable(newNick)) {
				throw new IllegalArgumentException("暱稱已被使用");
			}
			db.setNickname(newNick);
		}

		dao.update(db);
	}

	@Override
	@Transactional
	public void changePassword(Integer memberId, String newPasswordHash) {
		if (memberId == null)
			throw new IllegalArgumentException("memberId 不可為 null");
		requireNotBlank(newPasswordHash, "newPasswordHash 不可為空");

		MemberVO db = dao.findByPrimaryKey(memberId);
		if (db == null)
			throw new IllegalArgumentException("找不到該會員");

		db.setPasswordHash(newPasswordHash);
		dao.update(db);
	}

	@Override
	@Transactional
	public void remove(Integer memberId) {
		if (memberId == null)
			throw new IllegalArgumentException("memberId 不可為 null");
		dao.delete(memberId);
	}

	// ==================== 查詢 ====================

	@Override
	@Transactional(readOnly = true)
	public MemberVO get(Integer memberId) {
		if (memberId == null)
			return null;
		return dao.findByPrimaryKey(memberId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MemberVO> getAll() {
		return dao.getAll();
	}

	@Override
	@Transactional(readOnly = true)
	public MemberVO getByEmail(String email) {
		String norm = normEmail(email);
		if (norm == null || norm.isEmpty())
			return null;
		return dao.findByEmail(norm);
	}

	@Override
	@Transactional(readOnly = true)
	public MemberVO getByNickname(String nickname) {
		String n = safeTrim(nickname);
		if (n == null || n.isEmpty())
			return null;
		return dao.findByNickname(n);
	}

	// ==================== 檢核 ====================

	@Override
	@Transactional(readOnly = true)
	public boolean isNicknameAvailable(String nickname) {
		String n = safeTrim(nickname);
		if (n == null || n.isEmpty())
			return false;
		return dao.findByNickname(n) == null;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmailAvailable(String email) {
		String e = normEmail(email);
		if (e == null || e.isEmpty())
			return false;
		return dao.findByEmail(e) == null;
	}

	// ==================== 小工具（僅限本 Service 使用） ====================

	private static String normEmail(String email) {
		return email == null ? null : email.trim().toLowerCase();
	}

	private static String safeTrim(String s) {
		return s == null ? null : s.trim();
	}

	private static void requireNotBlank(String s, String msg) {
		if (s == null || s.trim().isEmpty())
			throw new IllegalArgumentException(msg);
	}
	
	
	// ==================== MemberConntroller增加 ====================

	public MemberVO getOneMember(Integer valueOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addMember(@Valid MemberVO memberVO) {
		// TODO Auto-generated method stub
		
	}

	public void updateMember(@Valid MemberVO memberVO) {
		// TODO Auto-generated method stub
		
	}

	public List<MemberVO> getAll(Map<String, String[]> map) {
		// TODO Auto-generated method stub
		return null;
	}
}
