package com.example.uni.auth;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.repo.UserRepository;
import com.example.uni.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final OAuthService oAuthService;
    private final CookieUtil cookieUtil;
    private final ObjectProvider<FirebaseTokenService> firebaseTokenService;
    private final UserService userService;

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

        String rotated = oAuthService.rotateRefresh(refresh);
        if (rotated != null) cookieUtil.setRefreshCookie(response, rotated);

        String access = oAuthService.issueAccessToken(userId);
        long expiresIn = oAuthService.getAccessTtlSeconds();
        return ResponseEntity.ok(Map.of("accessToken", access, "expiresIn", expiresIn));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieUtil.clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal String userId) {
        User u = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(userService.toResponse(u));
    }

    @GetMapping("/firebase/token")
    public ResponseEntity<?> firebaseToken(@AuthenticationPrincipal String userId) {
        FirebaseTokenService svc = firebaseTokenService.getIfAvailable();
        if (svc == null) {
            return ResponseEntity.status(503).body(Map.of(
                    "error", "FIREBASE_DISABLED",
                    "message", "Firebase is disabled on server"
            ));
        }
        try {
            String customToken = svc.createCustomToken(userId);
            return ResponseEntity.ok(Map.of("customToken", customToken));
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/kakao/unlink")
    public ResponseEntity<Void> unlink(@AuthenticationPrincipal String userId,
                                       HttpServletResponse response) {
        oAuthService.unlinkUser(UUID.fromString(userId));
        cookieUtil.clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }
}
