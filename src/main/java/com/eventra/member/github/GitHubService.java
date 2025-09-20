//package com.eventra.member.github;
//
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestClient;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestClientResponseException;
//
//import com.eventra.member.model.MemberRepository;
//import com.eventra.member.model.MemberVO;
//import com.properties.GitHubAuthProperties;
//import com.util.RandomRawPasswordGenerator;
//
//@Service
//@Transactional
//public class GitHubService {
//
//	private final String STATE = "RANDOM_STRING";
//	
//	private final RestClient REST_CLIENT_CODE_TO_AT;
//	private final RestClient REST_CLIENT_AT_TO_USER;
//	private final RestClient REST_CLIENT_AT_TO_USER_EMAILS;
//	private final GitHubAuthProperties GITHUB_AUTH_PROPS;
//	private final MemberRepository MEMBER_REPO;
//	private final BCryptPasswordEncoder PASSWORD_ENCODER;
//	
//	// util (not bean)
//	private static final RandomRawPasswordGenerator PASSWORD_GENERATOR = new RandomRawPasswordGenerator();  
//	
//	public GitHubService (GitHubAuthProperties githubAuthProperties, RestClient.Builder restClientBuilder, MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder){
//		this.GITHUB_AUTH_PROPS = githubAuthProperties;
//		this.REST_CLIENT_CODE_TO_AT = restClientBuilder.baseUrl(githubAuthProperties.urlCodeToToken())
//				.build();
//		this.REST_CLIENT_AT_TO_USER = restClientBuilder.baseUrl(githubAuthProperties.urlTokenToUser())
//				.build();
//		this.REST_CLIENT_AT_TO_USER_EMAILS = restClientBuilder.baseUrl(githubAuthProperties.urlTokenToUserEmails())
//				.build();
//		this.MEMBER_REPO = memberRepository;
//		this.PASSWORD_ENCODER = passwordEncoder;
//	}
//
//	// 1. 導向使用者去 GitHub 授權頁
//	// 2. 使用者授權後，GitHub 會 redirect 回你的 callback URL (GET)
//	// 3. 後端用 code 換 token (POST) -> https://github.com/login/oauth/access_token
//		// client_id, client_secret, code, redirect_uri
//		// 回傳會是一個 access_token（純字串格式，像 access_token=...&scope=...&token_type=bearer）。
//	// 4. 用 access_token 打 GitHub API 拿用戶資料
//		// GET https://api.github.com/user
//		// Header: Authorization: token {access_token}
//	// 5. 回傳 JSON
//		// {
//	  	// "login": "octocat",
//	  	// "id": 1,
//	  	// "avatar_url": "https://github.com/images/error/octocat_happy.gif",
//	  	// "name": "monalisa octocat",
//	  	// "email": "octocat@github.com"
//		// }
//	
//	public String validateState(String state) {
//		return null;
//	}
//	
//	public String extractToken(String code) {
//		MultiValueMap<String, String> map = fieldsBuilder(code);
//
//		GitHubExtractTokenReqDTO token = null;
//		
//		try {
//		    token = REST_CLIENT_CODE_TO_AT
//		        .post()
//		        .header("Accept", "application/json")
//		        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//		        .body(map)
//		        .retrieve()
//		        .body(GitHubExtractTokenReqDTO.class);
//
//		    if (token == null || token.getAccessToken() == null) {
//		        return  "token 為空值";
//		    }
//
//		} catch (RestClientResponseException e) {
//			System.out.println(e.toString());
//		} catch (RestClientException e) {
//			System.out.println(e.toString());
//		}
//
//
//		
////		System.out.println(token);
////		if(token != null) {
////			System.out.println("accessToken: " + token.getAccessToken());
////			System.out.println("scope: " + token.getScope());
////			System.out.println("tokenType: " + token.getTokenType());
////		}
//		
//		// https://api.github.com/user -> 這是 GitHub REST API v3 提供的 取得目前登入使用者資訊 的端點。
//			// 端點取決於 scope 權限要了什麼
//			// https://api/github.com/user/emails
//			// https://api/github.com/user/repos
//		// Bearer Token 是一種 HTTP Authorization Scheme -> 只要你持有此 token 表示你有權限
//			// Bearer Token 本身不是 header，它只是一串憑證。
//			// 它常常被 放在 Authorization header 裡，這是 OAuth 2.0 的標準用法。
//			// Authorization: <scheme> <credentials>
//				// <scheme> -> Basic= Base64 編碼的帳密; Bearer= OAuth 2.0 的 AT
//		// GitHub 的 AT 是隨機字串/ Opaque Token，本身不帶任何使用者資訊，不像 Google ID Token(JWT) 可以解出 email/name/subject
//		
//		// GitHub Server 會有一個 資料庫/快取，知道「這個 token 對應到哪個 user、scope、過期時間」。
//		// Token 只是一把「鑰匙」，它可以開很多不同的 API 門（依 scope 決定）。
//		// 有些 API 提供 JWT（自解碼），有些提供 opaque token（需回查），OAuth 2.0 標準允許兩種。
//		// GitHub 選擇 opaque token 模式，所以你必須「再打一次 API」才能知道是誰。
//		
//		String accessToken = token.getAccessToken(); 
//				
//		GitHubGetUserEmailReqDTO[] userEmailsDTOs = REST_CLIENT_AT_TO_USER_EMAILS
//				.get()
//				.header("Authorization", "Bearer " + accessToken)
//				.header("Accept", "application/vnd.github+json") // GitHub REST API 文件通用：（GitHub API 會根據 endpoint 回傳對應的 JSON 格式，不會因為 scope 不同要換 Accept。）
//				.retrieve()
//				.body(GitHubGetUserEmailReqDTO[].class);
//		
//		
//		GitHubGetUserReqDTO userDTO = REST_CLIENT_AT_TO_USER
//				.get()
//				.header("Authorization", "Bearer " + accessToken)
//				.header("Accept", "application/vnd.github+json")
//				.retrieve()
//				.body(GitHubGetUserReqDTO.class);
//		
//		
//		// 測試印出
//		for(GitHubGetUserEmailReqDTO emailDTO : userEmailsDTOs) System.out.println(emailDTO.getEmail());
//		System.out.println("========");
//		System.out.println(userDTO.toString());
//		
//		MemberVO memberVO = new MemberVO();
//		memberVO.setNickname(userDTO.getLogin()); // login -> nickname
//		memberVO.setGithubId(userDTO.getId()); // id -> github_id
//		memberVO.setProfilePic(userDTO.getAvatarUrl()); // avatarUrl -> profile_pic
//		memberVO.setPasswordHash(getRandomEncodedPassword());
//
//		return null;
//	}
//	
//	// util 亂數密碼產生 -> BCrypt 雜湊 -> 回傳
//	private String getRandomEncodedPassword() {
//		String rawPassword = PASSWORD_GENERATOR.generateRandomPassword();
//		String encodedPassword = PASSWORD_ENCODER.encode(rawPassword);
//		return encodedPassword;
//	}
//
//	private MultiValueMap<String, String> fieldsBuilder(String code) {
////		code=授權碼
////		&client_id=YOUR_CLIENT_ID
////		&client_secret=YOUR_CLIENT_SECRET
////		&redirect_uri=http://localhost:8080/auth/google/callback
////		&grant_type=authorization_code
//		
//		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//		map.add("code", code);
//		map.add("client_id", GITHUB_AUTH_PROPS.clientId());
//		map.add("client_secret", GITHUB_AUTH_PROPS.clientSecret());
//		map.add("redirect_uri", GITHUB_AUTH_PROPS.redirectUri()); // 需保持與 client step 1 去 github 時填入的 redirect uri 一致
//		map.add("state", STATE); // 需保持與 client step 1 去 github 時填入的 state 一致
//		
//		return map;
//	}
//
//}
