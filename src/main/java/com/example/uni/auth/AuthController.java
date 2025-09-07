package com.example.uni.auth;

import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @Autowired(required = false)
    private FirebaseTokenService firebaseTokenService;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${frontend.redirect-base}")
    private String frontendBase;

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
        String jwt = oAuthService.loginWithAuthorizationCode(code);
        cookieUtil.setAccessCookie(response, jwt);
        URI target = UriComponentsBuilder.fromUriString(frontendBase)
                .path(state.startsWith("/") ? state : "/" + state)
                .build(true).toUri();
        return ResponseEntity.status(302).location(target).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieUtil.clearAccessCookie(response);
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
    public ResponseEntity<?> firebaseToken(@AuthenticationPrincipal String userId) throws Exception {
        if (firebaseTokenService == null) {
            return ResponseEntity.status(503).body(Map.of(
                    "error", "FIREBASE_DISABLED",
                    "message", "Firebase is disabled on server"
            ));
        }
        String customToken = firebaseTokenService.createCustomToken(userId);
        return ResponseEntity.ok(Map.of("customToken", customToken));
    }
}
