package com.eventra.member.model;

import java.util.List;
import java.util.Set;

public class MemberService {

	private MemberDAO dao;

	public MemberService() {
		dao = new MemberDAO();
	}

	public List<MemberVO> getAll() {
		return dao.getAll();
	}

	public MemberVO getOneMember(Integer memberId) {
		return dao.findByPrimaryKey(memberId);
	}

	public Set<MemberVO> getOrdersByMemberId(Integer memberId) {
		return null;
	}

	public void deleteMember(Integer memberId) {
		dao.delete(memberId);
	}
}
