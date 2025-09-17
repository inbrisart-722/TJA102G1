package com.eventra.member.mapper;

import com.eventra.member.dto.MemberResponse;
import com.eventra.member.dto.UpdateProfileRequest;
import com.eventra.member.model.MemberVO;

public final class MemberMapper {
	private MemberMapper() {
	}

	public static MemberResponse toResponse(MemberVO m) {
		MemberResponse r = new MemberResponse();
		r.setId(m.getMemberId());
		r.setEmail(m.getEmail());
		r.setNickname(m.getNickname());
		r.setFullName(m.getFullName());
		r.setCreatedAt(m.getCreatedAt());
		r.setUpdatedAt(m.getUpdatedAt());
		return r;
	}

	public static void applyPatch(MemberVO m, UpdateProfileRequest req) {
		if (req.getFullName() != null)
			m.setFullName(req.getFullName());
		if (req.getPhoneNumber() != null)
			m.setPhoneNumber(req.getPhoneNumber());
		if (req.getAddress() != null)
			m.setAddress(req.getAddress());
		if (req.getProfilePic() != null)
			m.setProfilePic(req.getProfilePic());
	}
}
