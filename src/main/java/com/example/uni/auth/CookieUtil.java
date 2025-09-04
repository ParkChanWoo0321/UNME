package com.example.uni.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {

    @Value("${auth.cookie.name}")
    private String cookieName;

    @Value("${auth.cookie.domain}")
    private String domain;

    @Value("${auth.cookie.secure}")
    private boolean secure;

    @Value("${auth.cookie.max-age}")
    private long maxAgeSeconds;

    @Value("${auth.cookie.same-site}")
    private String sameSite; // None / Lax / Strict

    public void setAccessCookie(HttpServletResponse response, String jwt) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, jwt)
                .domain(domain)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearAccessCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .domain(domain)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
