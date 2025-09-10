package com.example.uni.auth;

import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
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

    @Value("${kakao.client-id}")   private String kakaoClientId;
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
                .build(true).toUri();
        return ResponseEntity.status(302).location(authorize).build();
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<Void> callback(@RequestParam("code") String code,
                                         @RequestParam(value = "state", required = false, defaultValue = "/") String state,
                                         HttpServletResponse response) {
        String refresh = oAuthService.loginWithAuthorizationCode(code); // refresh 발급
        cookieUtil.setRefreshCookie(response, refresh);                  // ★ HttpOnly 쿠키
        URI target = UriComponentsBuilder.fromUriString(frontendBase)
                .path(state.startsWith("/") ? state : "/" + state)
                .build(true).toUri();
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
    public ResponseEntity<?> me(@AuthenticationPrincipal String userId) {
        User u = userRepository.findById(UUID.fromString(userId)).orElseThrow();
        return ResponseEntity.ok(Map.of(
                "userId", u.getId(),
                "profileComplete", u.isProfileComplete(),
                "credits", u.getMatchCredits()
        ));
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
            throw new com.example.uni.common.exception.ApiException(
                    com.example.uni.common.exception.ErrorCode.INTERNAL_SERVER_ERROR
            );
        }
    }
}