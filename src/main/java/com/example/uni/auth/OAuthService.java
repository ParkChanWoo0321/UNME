package com.example.uni.auth;

import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final KakaoOAuthClient kakao;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshRepo; // ★ 추가

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${jwt.refresh-ttl-seconds}")
    private long refreshTtlSeconds; // ★ 추가

    // ★ 디바이스 포함: 로그인 → 사용자 생성/업데이트 → 디바이스별 리프레시 발급/저장
    public String loginWithAuthorizationCode(String code, String deviceId) {
        var token = kakao.exchangeCodeForToken(code, redirectUri);
        var me = kakao.me(token.getAccess_token());
        final String kakaoId = String.valueOf(me.getId());
        final Gender resolvedGender = resolveGender(me);

        User user = userRepository.findByKakaoId(kakaoId)
                .map(u -> {
                    if (resolvedGender != null && u.getGender() == null) u.setGender(resolvedGender);
                    return u;
                })
                .orElseGet(() -> User.builder()
                        .kakaoId(kakaoId)
                        .gender(resolvedGender)
                        .profileComplete(false)
                        .matchCredits(0)
                        .build());
        user = userRepository.save(user);
        UUID uid = user.getId();

        // 리프레시 토큰 문자열 생성(JWT든 난수든 상관없음)
        String refresh = jwtProvider.generateRefresh(uid.toString());

        // DB에 디바이스별로 저장 (다른 기기 세션에 영향 없음)
        RefreshToken rt = RefreshToken.builder()
                .userId(uid.toString())
                .deviceId(deviceId)
                .token(refresh)
                .expiresAt(Instant.now().plusSeconds(refreshTtlSeconds))
                .revoked(false)
                .build();
        refreshRepo.save(rt);

        return refresh;
    }

    // ★ 디바이스 포함: 저장소/서명 모두 검증
    public String validateRefreshAndGetUserId(String refreshJwt, String deviceId) {
        // 1) 서명/유효성 검증 (subject = userId)
        String subjectUserId = jwtProvider.validateAndGetSubject(refreshJwt, JwtProvider.TokenType.REFRESH);

        // 2) 저장소 조회로 활성 토큰인지 확인(디바이스 매칭 포함)
        RefreshToken rt = refreshRepo.findActiveByToken(refreshJwt)
                .orElseThrow(() -> new com.example.uni.common.exception.ApiException(ErrorCode.UNAUTHORIZED));
        if (!rt.getUserId().equals(subjectUserId)) {
            throw new com.example.uni.common.exception.ApiException(ErrorCode.UNAUTHORIZED);
        }
        if (!rt.getDeviceId().equals(deviceId)) {
            throw new com.example.uni.common.exception.ApiException(ErrorCode.UNAUTHORIZED);
        }
        if (rt.isExpired() || rt.isRevoked()) {
            throw new com.example.uni.common.exception.ApiException(ErrorCode.UNAUTHORIZED);
        }
        return rt.getUserId();
    }

    // ★ 디바이스 포함: 해당 디바이스 세션만 회전(다른 기기 유지)
    public String rotateRefresh(String oldRefresh, String deviceId) {
        // 저장소에서 활성 토큰 찾기 + 디바이스 확인
        RefreshToken rt = refreshRepo.findActiveByToken(oldRefresh)
                .orElseThrow(() -> new com.example.uni.common.exception.ApiException(ErrorCode.UNAUTHORIZED));
        if (!rt.getDeviceId().equals(deviceId)) {
            throw new com.example.uni.common.exception.ApiException(ErrorCode.UNAUTHORIZED);
        }
        if (rt.isExpired() || rt.isRevoked()) {
            throw new com.example.uni.common.exception.ApiException(ErrorCode.UNAUTHORIZED);
        }

        // 이전 토큰 폐기
        rt.revoke();
        refreshRepo.save(rt);

        // 새 토큰 생성/저장(동일 userId+deviceId)
        String next = jwtProvider.generateRefresh(rt.getUserId());
        RefreshToken nextRt = RefreshToken.builder()
                .userId(rt.getUserId())
                .deviceId(rt.getDeviceId())
                .token(next)
                .expiresAt(Instant.now().plusSeconds(refreshTtlSeconds))
                .revoked(false)
                .build();
        refreshRepo.save(nextRt);

        return next;
    }

    public String issueAccessToken(String userId) {
        return jwtProvider.generateAccess(userId);
    }

    public long getAccessTtlSeconds() { return jwtProvider.getAccessTtlSeconds(); }

    private Gender resolveGender(KakaoOAuthClient.KakaoUser me) {
        var acc = me.getKakaoAccount();
        if (acc == null) return null;
        if (Boolean.TRUE.equals(acc.getHasGender()) && acc.getGender() != null) {
            String g = acc.getGender();
            if ("male".equalsIgnoreCase(g)) return Gender.MALE;
            if ("female".equalsIgnoreCase(g)) return Gender.FEMALE;
        }
        return null;
    }
}