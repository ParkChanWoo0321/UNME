// com/example/uni/auth/AuthController.java
package com.example.uni.auth;

import com.example.uni.common.exception.ApiException;
import com.example.uni.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthService oAuthService;
    private final CookieUtil cookieUtil;
    private final UserService userService;
    private final FirebaseBridgeService firebaseBridge;

    @Value("${kakao.client-id}")    private String kakaoClientId;
    @Value("${kakao.redirect-uri}") private String redirectUri;
    @Value("${frontend.redirect-base}") private String frontendBase;

    @GetMapping("/kakao/login")
    public ResponseEntity<Void> login(@RequestParam(value = "next", required = false) String next) {
        String state = next == null ? "/" : next;
        URI authorize = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", kakaoClientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .build()
                .toUri();
        return ResponseEntity.status(302).location(authorize).build();
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<Void> callback(@RequestParam("code") String code,
                                         @RequestParam(value = "state", required = false, defaultValue = "/") String state,
                                         HttpServletResponse response) {
        OAuthService.Tokens tokens = oAuthService.loginWithAuthorizationCode(code);
        cookieUtil.setRefreshCookie(response, tokens.refresh());

        String s = (state == null) ? "/" : state.trim();
        s = URLDecoder.decode(s, StandardCharsets.UTF_8);
        if (s.contains("\r") || s.contains("\n")) s = "/";
        s = s.replaceFirst("(?i)^https:/(?!/)", "https://");
        s = s.replaceFirst("(?i)^http:/(?!/)",  "http://");
        if (s.startsWith("//")) s = "https:" + s;

        URI target;
        try {
            URI u = URI.create(s);
            if (u.isAbsolute()) {
                String host = u.getHost();
                boolean allowed = host != null && (
                        host.equals("likelionhsu.co.kr") ||
                                host.endsWith(".likelionhsu.co.kr") ||
                                host.equals("localhost")
                );
                if (allowed) {
                    target = UriComponentsBuilder.fromUri(u)
                            .fragment("access=" + tokens.access())
                            .build(true).toUri();
                } else {
                    target = UriComponentsBuilder.fromUriString(frontendBase)
                            .fragment("access=" + tokens.access())
                            .build(true).toUri();
                }
            } else {
                target = UriComponentsBuilder.fromUriString(frontendBase)
                        .path(s.startsWith("/") ? s : "/" + s)
                        .fragment("access=" + tokens.access())
                        .build(true).toUri();
            }
        } catch (IllegalArgumentException e) {
            target = UriComponentsBuilder.fromUriString(frontendBase)
                    .fragment("access=" + tokens.access())
                    .build(true).toUri();
        }

        return ResponseEntity.status(302).location(target).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> reissueAccess(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtil.resolveRefreshFromRequest(request);
        if (refresh == null) return ResponseEntity.status(401).body(Map.of("error","NO_REFRESH"));

        String userId = oAuthService.validateRefreshAndGetUserId(refresh);
        try {
            String rotated = oAuthService.rotateRefresh(refresh); // 비활성 유저면 여기서 예외
            if (rotated != null) cookieUtil.setRefreshCookie(response, rotated);

            String access = oAuthService.issueAccessToken(userId); // 비활성 유저면 예외
            long expiresIn = oAuthService.getAccessTtlSeconds();
            return ResponseEntity.ok(Map.of("accessToken", access, "expiresIn", expiresIn));
        } catch (ApiException e) {
            cookieUtil.clearRefreshCookie(response);
            return ResponseEntity.status(401).body(Map.of("error","NO_ACTIVE_USER"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieUtil.clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String,Object>> me(@AuthenticationPrincipal String userId) {
        var u = userService.getActive(Long.valueOf(userId));
        String firebaseCustomToken = firebaseBridge.createCustomToken(userId);

        return ResponseEntity.ok(Map.of(
                "firebaseCustomToken", firebaseCustomToken,
                "user", userService.toResponse(u)
        ));
    }

    @DeleteMapping("/kakao/unlink")
    public ResponseEntity<Void> unlink(@AuthenticationPrincipal String userId,
                                       HttpServletResponse response) {
        oAuthService.unlinkUser(Long.valueOf(userId));
        cookieUtil.clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }
}
