package com.example.uni.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private final Key key;
    private final long ttlMillis;

    public JwtProvider(
            @Value("${jwt.secret:change-me-change-me-change-me-change-me}") String secret,
            @Value("${jwt.ttl-seconds:2592000}") long ttlSeconds // 30d
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlMillis = ttlSeconds * 1000L;
    }

    /** JWT 검증 + subject 추출 */
    public String validateAndGetSubject(String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        return claims.getSubject();
    }

    /** subject 만 넣어 발급 */
    public String generateToken(String subject) {
        return generate(subject, null);
    }

    /** subject + kakaoId(claim) 넣어 발급 */
    public String generate(String subject, String kakaoId) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ttlMillis)));

        if (kakaoId != null) {
            builder.addClaims(Map.of("kakaoId", kakaoId));
        }

        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /** 필요 시 파싱용 */
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}