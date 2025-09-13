package com.example.uni.auth;

import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final KakaoOAuthClient kakao;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // 변경: access와 refresh를 함께 반환
    public Tokens loginWithAuthorizationCode(String code) {
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

        String access = jwtProvider.generateAccess(uid.toString());
        String refresh = jwtProvider.generateRefresh(uid.toString());
        return new Tokens(access, refresh);
    }

    public String issueAccessToken(String userId) {
        return jwtProvider.generateAccess(userId);
    }

    public long getAccessTtlSeconds() { return jwtProvider.getAccessTtlSeconds(); }

    public String validateRefreshAndGetUserId(String refreshJwt) {
        return jwtProvider.validateAndGetSubject(refreshJwt, JwtProvider.TokenType.REFRESH);
    }

    public String rotateRefresh(String oldRefresh) {
        String userId = jwtProvider.validateAndGetSubject(oldRefresh, JwtProvider.TokenType.REFRESH);
        return jwtProvider.generateRefresh(userId);
    }

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

    // access + refresh DTO (새 파일 불필요: 내부 record로 정의)
    public record Tokens(String access, String refresh) {}
}
