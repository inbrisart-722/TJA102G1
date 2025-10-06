package com.eventra.exhibitor;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.time.Instant;
import java.util.UUID;

public class ExhibitorResetTokenUtil {
  private final String appSecret;
  private final long ttlSeconds;

  public ExhibitorResetTokenUtil(String appSecret, long ttlSeconds) {
    this.appSecret = appSecret;
    this.ttlSeconds = ttlSeconds;
  }

  private Key deriveKey(String passwordHash) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(appSecret.getBytes(), "HmacSHA256"));
      byte[] keyBytes = mac.doFinal(passwordHash.getBytes());
      return Keys.hmacShaKeyFor(keyBytes); // >= 32 bytes
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String create(Integer exhibitorId, String email, String passwordHash) {
    Instant now = Instant.now();
    Key key = deriveKey(passwordHash);
    return Jwts.builder()
      .setSubject(String.valueOf(exhibitorId))        // 0.11 用 setXxx(...)
      .claim("em", email)
      .setId(UUID.randomUUID().toString())
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
      .signWith(key, SignatureAlgorithm.HS256)        // 0.11 用 SignatureAlgorithm
      .compact();
  }

  public Jws<Claims> parse(String token, String currentPasswordHash) {
    Key key = deriveKey(currentPasswordHash);
    return Jwts.parserBuilder()                       // 0.11 用 parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token);
  }
}

