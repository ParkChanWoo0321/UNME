package com.example.uni.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    public enum TokenType { ACCESS, REFRESH }

    private final Key key;
    private final long accessTtlMillis;
    private final long refreshTtlMillis;

    public JwtProvider(
            @Value("${jwt.secret:change-me-change-me-change-me-change-me}") String secret,
            @Value("${jwt.access-ttl-seconds:1800}") long accessTtlSeconds,
            @Value("${jwt.refresh-ttl-seconds:2592000}") long refreshTtlSeconds
    ) {
        byte[] k;
        try {
            k = Base64.getDecoder().decode(secret);
            if (k.length < 32) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            k = secret.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(k);
        this.accessTtlMillis = accessTtlSeconds * 1000L;
        this.refreshTtlMillis = refreshTtlSeconds * 1000L;
    }

    public String generateAccess(String subject) { return generate(subject, TokenType.ACCESS); }
    public String generateRefresh(String subject) { return generate(subject, TokenType.REFRESH); }

    private String generate(String subject, TokenType typ) {
        Instant now = Instant.now();
        long ttl = typ == TokenType.ACCESS ? accessTtlMillis : refreshTtlMillis;
        return Jwts.builder()
                .setSubject(subject)
                .claim("typ", typ.name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ttl)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndGetSubject(String jwt, TokenType expected) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        String actual = String.valueOf(claims.get("typ"));
        if (!expected.name().equals(actual)) throw new JwtException("invalid token type");
        return claims.getSubject();
    }

    public String validateAccessAndGetSubject(String jwt) {
        return validateAndGetSubject(jwt, TokenType.ACCESS);
    }

    public long getAccessTtlSeconds() { return accessTtlMillis / 1000L; }
}