package com.security.jwt; // 📦 定義這個類別所在的 package，方便組織與管理

import java.security.Key; // Java Security 的 Key 介面，用來存放簽章的秘密金鑰
import java.time.Duration; // Java 8+ 的 Duration，方便設定有效時間 (例如 15 分鐘、7 天)
import java.util.Date; // Java 的 Date 類別，用於 JWT 的簽發與到期時間

import org.springframework.stereotype.Component; // Spring 註解，表示這是一個可被掃描的 Spring Bean

import com.properties.JwtProperties;

import io.jsonwebtoken.JwtException; // JWT 處理時可能丟出的例外
import io.jsonwebtoken.Jwts; // JJWT (io.jsonwebtoken) 的核心工具類，負責建構/解析 Token
import io.jsonwebtoken.SignatureAlgorithm; // 簽章演算法的 Enum，這裡會用 HS256
import io.jsonwebtoken.security.Keys; // 提供產生/驗證安全金鑰的工具類

@Component
public class JwtUtil {

//	這個 JwtUtil 幫你做了五件事：
	// 建立金鑰（對稱式 HMAC-SHA256）。
	// 產生 Access Token（短效）。
	// 產生 Refresh Token（長效，並加上 typ=refresh 區分）。
	// 驗證 Token（簽章、Issuer、過期時間）。
	// 提取資訊（username / 是否 refresh token）。

	// ⚠️ 秘密金鑰（對稱式）: 實務上不要硬編碼在程式裡！
	// 應改用環境變數、Vault、KMS 等安全來源
	// JWT 的 HMAC-SHA256 要求至少 256-bit 長度
	private final String SECRET;
	// Token 的 "iss" (Issuer) 欄位，通常用來標識簽發者（可用系統名稱/公司名稱）
	private final String ISSUER;

	// 建立金鑰物件，使用 SECRET 轉換成 byte[]，再交給 JJWT 的工具類 Keys 產生 HMAC-SHA 金鑰
	private final Key key;

	public JwtUtil(JwtProperties jwtProps) {
		this.SECRET = jwtProps.secret();
		this.ISSUER = jwtProps.issuer();
		this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
	}

	// ============================
	// 產生 Access Token（短效用，例如 15 分鐘）
	// ============================
	public String generateAccess(String subject, Duration ttl) {
		long now = System.currentTimeMillis(); // 取得當下時間 (毫秒)
		System.out.println("=============" + subject + "=============");

		return Jwts.builder() // 建立 JWT 的 builder
				.setSubject(subject) // 設定 Token 主體 (目前改存 memberId)
				.setIssuer(ISSUER) // 設定簽發者 (iss 欄位)
				.setIssuedAt(new Date(now)) // 簽發時間 (iat 欄位)
				.setExpiration(new Date(now + ttl.toMillis())) // 過期時間 (exp 欄位)
				.signWith(key, SignatureAlgorithm.HS256) // 用 key + HS256 演算法簽章
				.compact(); // 生成 Token (字串格式)
	}

	// ============================
	// 產生 Refresh Token（長效用，例如 7 天）
	// ============================
	public String generateRefresh(String subject, Duration ttl) {
		long now = System.currentTimeMillis(); // 取得當下時間

		return Jwts.builder() // 建立 JWT builder
				.setSubject(subject) // 設定主體 (使用者身分)
				.setIssuer(ISSUER) // 設定簽發者
				.setIssuedAt(new Date(now)) // 簽發時間
				.setExpiration(new Date(now + ttl.toMillis())) // 過期時間
				.claim("typ", "refresh") // 🔑 自訂 claim：加一個 "typ" 欄位，值是 "refresh"
											// 這樣後續可以區分 Access Token / Refresh Token
				.signWith(key, SignatureAlgorithm.HS256) // 用 HMAC-SHA256 簽章
				.compact(); // 最後輸出 Token 字串
	}

	// ============================
	// 驗證 Token 是否有效
	// ============================
	public boolean validate(String token) {
		try {
			// 嘗試解析 Token，並要求：
			// 1. 簽章必須正確
			// 2. iss (Issuer) 必須等於設定的 ISSUER
			// 3. exp (Expiration) 自動驗證是否過期
			Jwts.parserBuilder().setSigningKey(key) // 使用相同金鑰驗證
					.requireIssuer(ISSUER) // 限制簽發者必須正確
					.build().parseClaimsJws(token); // 嘗試解析並驗證 Token，若失敗會丟例外

			return true; // 如果沒有丟例外，表示 Token 有效
		} catch (JwtException | IllegalArgumentException e) {
			System.out.println("JwtUtil: " + e.toString());
			// JwtException: 例如簽章錯誤、過期、格式錯誤
			// IllegalArgumentException: 例如 token 是 null 或空字串
			return false; // 驗證失敗回傳 false
		}
	}

	// ============================
	// 從 Token 取出 username (subject)
	// ============================
	public String getUsername(String token) {
		try {
			String sub = Jwts.parserBuilder().setSigningKey(key) // 使用相同金鑰驗證
					.requireIssuer(ISSUER) // 要求 Issuer 正確
					.build().parseClaimsJws(token) // 解析並驗證 Token
					.getBody() // 取得 Token 的 Payload (Claims)
					.getSubject(); // 從 Claims 中取出 subject (username)
			return (sub == null || sub.isBlank()) ? null : sub;
		} catch (JwtException e) {
			System.out.println("JwtUtil: " + e.toString());
			return null;
		}
	}

	// ============================
	// 檢查 Token 是否為 Refresh Token
	// ============================
	public boolean isRefreshToken(String token) {
		return "refresh".equals( // 檢查 typ claim 是否等於 "refresh"
				Jwts.parserBuilder().setSigningKey(key) // 使用相同金鑰驗證
						.requireIssuer(ISSUER) // 要求 Issuer 正確
						.build().parseClaimsJws(token) // 解析並驗證 Token
						.getBody() // 取得 Payload
						.get("typ", String.class) // 取出 typ 欄位 (自訂 claim)
		);
	}
}
