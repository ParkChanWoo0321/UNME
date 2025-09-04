package com.example.uni.auth;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final KakaoMobileLoginService mobileLoginService;
    private final JwtProvider jwtProvider;

    /**
     * 모바일: 카카오 access_token을 Authorization 헤더로 받아 우리 JWT 발급
     * 요청 헤더: Authorization: Bearer <KAKAO_ACCESS_TOKEN>
     * 응답: { "jwt": "<OUR_JWT>", "onboardingRequired": true|false }
     */
    @PostMapping("/mobile/login")
    public ResponseEntity<Map<String, Object>> mobileLogin(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        String kakaoAccessToken = authorization.substring(7);

        // 1) 카카오 토큰으로 로그인 처리 → 우리 JWT 발급
        String jwt = mobileLoginService.loginWithKakaoAccessToken(kakaoAccessToken);

        // 2) 우리 JWT에서 userId 추출 → 온보딩 필요 여부 계산
        String userId = jwtProvider.validateAndGetSubject(jwt);
        boolean onboardingRequired = userRepository.findById(UUID.fromString(userId))
                .map(u -> !u.isProfileComplete())
                .orElse(true);

        return ResponseEntity.ok(Map.of(
                "jwt", jwt,
                "onboardingRequired", onboardingRequired
        ));
    }

    /**
     * JWT 인증 후 내 상태 요약
     * 요청 헤더: Authorization: Bearer <OUR_JWT>
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal String userId) {
        User u = userRepository.findById(UUID.fromString(userId)).orElseThrow();
        return ResponseEntity.ok(Map.of(
                "userId", u.getId(),
                "profileComplete", u.isProfileComplete(),
                "credits", u.getMatchCredits()
        ));
    }
}
