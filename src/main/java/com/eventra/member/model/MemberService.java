package com.eventra.member.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.member.verif.model.ForgotPasswordReqDTO;
import com.eventra.member.verif.model.RegisterReqDTO;
import com.eventra.member.verif.model.UpdateInfoReqDTO;
import com.util.RandomRawPasswordGenerator;

@Service
@Transactional
public class MemberService {
	
//	@PersistenceContext
//	private EntityManager entityManager;
	
	private final MemberRepository MEMBER_REPO;
	private final MemberRedisRepository MEMBER_REDIS_REPO;
	private final PasswordEncoder PASSWORD_ENCODER; 
	private final String DEFAULT_PROFILE_PIC;
	
	public MemberService(MemberRepository memberRepository, MemberRedisRepository memberRedisRepository, PasswordEncoder passwordEncoder, @Value("${default.profile-pic}") String defaultProfilePic) {
		this.MEMBER_REPO = memberRepository;
		this.MEMBER_REDIS_REPO = memberRedisRepository;
		this.PASSWORD_ENCODER = passwordEncoder;
		this.DEFAULT_PROFILE_PIC = defaultProfilePic;
	}
	
	public Boolean checkIfLineUserIdBound(String lineUserId) {
		if(MEMBER_REPO.findByLineUserId(lineUserId).orElse(null) == null)
			return false;
		else return true;
	}
	
	public void setLineUserId(String lineUserId, Integer memberId) {
		MemberVO member = MEMBER_REPO.findById(memberId).orElseThrow();
		member.setLineUserId(lineUserId);
	}
	
	public String getMyProfilePic(Integer memberId) {
		MemberVO member = MEMBER_REPO.findById(memberId).orElse(null);
		if(member == null) return DEFAULT_PROFILE_PIC;
		else if(member.getProfilePic() != null) return member.getProfilePic();
		else return DEFAULT_PROFILE_PIC;
	}
	public String forgotPassword(ForgotPasswordReqDTO req) {
		String token = req.getToken();
		String password = req.getPassword();
		String password_hash = PASSWORD_ENCODER.encode(password);
		
		String email = MEMBER_REDIS_REPO.findEmailByToken(token);
		if(email == null) return null;
		
		MemberVO memberVO = MEMBER_REPO.findByEmail(email).orElse(null);
		if(memberVO == null) return null;
		
		memberVO.setPasswordHash(password_hash);
		
		return email;
	}
	
	public Boolean checkIfPasswordCorrect(String password, Integer memberId) {
		// 資料庫密碼
		MemberVO member = MEMBER_REPO.findById(memberId).orElseThrow(() -> new RuntimeException("User not found!"));
		String DBPasswordHash = member.getPasswordHash();
		// 使用者此次輸入的密碼
		if(!PASSWORD_ENCODER.matches(password, DBPasswordHash)) return false;
		else return true;
	}
	
	public boolean checkIfMember(String email) {
		MemberVO memberVO = MEMBER_REPO.findByEmail(email).orElse(null);
		if(memberVO == null) return false; // 不是會員
		else return true; // 是會員（已經在會員 DB 中）
	}
	
	public String register(RegisterReqDTO req) {
		// const send_data = { token , password, nickname };
		String token = req.getToken();
		
		String password = req.getPassword();
		String nickname = req.getNickname();
		String email = MEMBER_REDIS_REPO.findEmailByToken(token);
		if(email == null) return null;
		
		String password_hash = PASSWORD_ENCODER.encode(password);
		
		MemberVO member = new MemberVO.Builder(email, password_hash, nickname).build();
		MEMBER_REPO.save(member);
		
		// 這顆 token 已無用途
		MEMBER_REDIS_REPO.deleteToken(token);
		// 這個 email 的冷卻時間也可以刪除 -> 避免有人註冊完後馬上改密碼等等，不該擋他！
		MEMBER_REDIS_REPO.deleteResendLimit(email);
		
		return email;
	}
	
	public void resetPassword(String password, Integer memberId) {
		MemberVO member = MEMBER_REPO.findById(memberId).orElseThrow();
		// IllegalArgumentException
		String password_hash = PASSWORD_ENCODER.encode(password);
		member.setPasswordHash(password_hash);
	}
	
	public void updateMemberPhoto(String src, Integer memberId) throws NoSuchElementException {
		MemberVO member = MEMBER_REPO.findById(memberId).orElseThrow();
		member.setProfilePic(src);
	}
	
	public void updateMemberInfo(UpdateInfoReqDTO req, Integer memberId) {
		// 1. 從 id 拿 member 物件
		MemberVO member = MEMBER_REPO.findById(memberId).orElseThrow();
		// 2. 把更新資料塞入 managed object (因為 @Transactional）
		if(!Objects.equals(member.getFullName(), req.getFullName())) member.setFullName(req.getFullName());
		if(!Objects.equals(member.getNickname(), req.getNickname())) member.setNickname(req.getNickname());
		if(!Objects.equals(member.getGender(), req.getGender())) member.setGender(req.getGender());
		if(!Objects.equals(member.getPhoneNumber(), req.getPhoneNumber())) member.setPhoneNumber(req.getPhoneNumber());
		if(!Objects.equals(member.getBirthDate(), req.getBirthDate())) member.setBirthDate(req.getBirthDate());
		if(!Objects.equals(member.getAddress(), req.getAddress())) member.setAddress(req.getAddress());
		// 3. 存回去
		MEMBER_REPO.save(member);
	}
	
	public MemberVO loadOrCreateFromOAuth2(String provider, OAuth2User user) {
		
		/* =========== 1. 嘗試用 provider ID 從資料庫中找出 memberVO，有就直接回傳 =========== */
		MemberVO memberVO = null;
		String providerId = null; 
		
		switch(provider) {
			case "google" -> {
				providerId = user.getAttributes().get("sub").toString();
				memberVO = MEMBER_REPO.findByGoogleId(providerId).orElse(null);
			}
			case "github" -> {
				providerId = user.getAttributes().get("id").toString();
				memberVO = MEMBER_REPO.findByGithubId(providerId).orElse(null);
			}
			case "facebook" -> {
				providerId = user.getAttributes().get("id").toString();
				memberVO = MEMBER_REPO.findByFacebookId(providerId).orElse(null);
			}
		}
		
		if(memberVO != null) return memberVO;
		
		/* =========== 2. provider ID 找不到！試看看透過 email 去找，且 merge =========== */
		
//		String email = null;
//		
//		switch(provider) {
//			case "google" -> {
//				if( Boolean.TRUE.equals(user.getAttribute("email_verified")) ) {
//					email = user.getAttribute("email");
//					memberVO = MEMBER_REPO.findByEmail(email).orElse(null);
//				}
//			}
//			// github 的 email 只放 scope = read:user 沒辦法驗證，不能亂 merge
//		}
//		
//		if(memberVO != null) {
//			if(memberVO.getGoogleId() == null) {
//				memberVO.setGoogleId(providerId);
//				MEMBER_REPO.save(memberVO);
//			}
//			return memberVO;
//		}
		
		/* =========== 3. email 也找不到！所以以下開始建立新 member 並塞入資料 =========== */
		memberVO = new MemberVO();
		
		Map<String, Object> infoMap = user.getAttributes();
		
		switch(provider) {
		
			case "google" -> {
				memberVO.setGoogleId(providerId);
//				if( Boolean.TRUE.equals(user.getAttribute("email_verified")) ) 
//					memberVO.setEmail( (String) infoMap.get("email") );
				memberVO.setFullName( (String) infoMap.get("name") );
				memberVO.setNickname( Objects.toString(infoMap.get("given_name"), "Google 匿名使用者" ));
				memberVO.setProfilePic( (String) infoMap.get("picture") );
				memberVO.setPasswordHash(getRandomEncodedPassword());
			}
			case "github" -> {
				memberVO.setGithubId(providerId);
				memberVO.setNickname( Objects.toString(infoMap.get("name"), Objects.toString(infoMap.get("login"), "GitHub 匿名使用者" )));
				memberVO.setProfilePic( (String) infoMap.get("avatar_url") );
				memberVO.setPasswordHash(getRandomEncodedPassword());
			}
			case "facebook" -> {
				memberVO.setFacebookId(providerId);
				memberVO.setNickname( (Objects.toString(infoMap.get("name"), "Facebook 匿名使用者")) );
				
				// Facebook 直接存 uri 會過期，其實要存其中的 ASID
					// GET https://graph.facebook.com/{asid}/picture?type=large&redirect=false&access_token={user_access_token}
						//  回應範例(json擷取部分）: "url": "https://scontent.xx.fbcdn.net/v/t1.6435-9/abc123...jpg" ...
				
				Map<String, Object> picture = (Map<String, Object>) infoMap.get("picture");
				if (picture != null) {
				    Map<String, Object> data = (Map<String, Object>) picture.get("data");
				    if (data != null) {
				        String url = (String) data.get("url"); // <-- 這裡才是字串（ fb 回來要取圖片 包了很多層 )
				        memberVO.setProfilePic(url);

				        String asid = getAsid(url);
				        memberVO.setAsid(asid);
				    }
				}
				
				memberVO.setPasswordHash(getRandomEncodedPassword());
			}
		}
		
		// 存回以後再回到 success handler in SecurityConfig
		MEMBER_REPO.save(memberVO);
		
		return memberVO;
	}
	public GetMemberInfoResDTO getMemberInfo(Integer memberId) {
		MemberVO member = MEMBER_REPO.findById(memberId).orElseThrow();
		
		GetMemberInfoResDTO res = new GetMemberInfoResDTO.Builder()
				.email(member.getEmail())
				.fullName(member.getFullName())
				.nickname(member.getNickname())
				.gender(member.getGender())
				.phoneNumber(member.getPhoneNumber())
				.birthDate(member.getBirthDate())
				.address(member.getAddress())
				.profilePic(member.getProfilePic())
				.lineUserId(member.getLineUserId())
				.githubId(member.getGithubId())
				.googleId(member.getGoogleId())
				.facebookId(member.getFacebookId())
				.build();
		
		return res;
	}
	
	public MemberVO findByEmail() {
		return null;
	}
	
	// ........
	
	// OAuth2 配合 Spring Security UsernamePasswordAuthenticationToken 的假密碼
	private String getRandomEncodedPassword() {
		String rawPassword = RandomRawPasswordGenerator.generateRandomPassword();
		String encodedPassword = PASSWORD_ENCODER.encode(rawPassword);
		return encodedPassword;
	}
	
	// Facebook 配合圖片很快過期的問題，需要從 uri 提取的 asid（但這次專題應該會忽略後續的操作，基本 10/18 後過期）
	private String getAsid(String url) {
		URI uri = null;
		try {uri = new URI(url);}
		catch(URISyntaxException e) {System.out.println(e.toString());}
		
		if(uri == null) return null;
        String query = uri.getQuery(); // asid=4296615037228444&height=50&...
        
        if(query == null) return null;
        Map<String, String> params = new HashMap<>();
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            params.put(kv[0], kv[1]);
        }
        
        return params.get("asid");
	}
}
