package com.example.uni.auth;

import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final KakaoOAuthClient kakao;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public Tokens loginWithAuthorizationCode(String code) {
        var token = kakao.exchangeCodeForToken(code, redirectUri);
        var me = kakao.me(token.getAccess_token());
        final String kakaoId = String.valueOf(me.getId());

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> User.builder()
                        .kakaoId(kakaoId)
                        .email(me.getKakaoAccount().getEmail())
                        .nickname(me.getKakaoAccount().getProfile().getNickname())
                        .profileComplete(false)
                        .matchCredits(0)
                        .build());
        user = userRepository.save(user);

        Long uid = user.getId(); // ← Long
        String access  = jwtProvider.generateAccess(String.valueOf(uid));
        String refresh = jwtProvider.generateRefresh(String.valueOf(uid));
        return new Tokens(access, refresh);
    }

    public String issueAccessToken(String userId) {
        return jwtProvider.generateAccess(userId);
    }

    public long getAccessTtlSeconds() {
        return jwtProvider.getAccessTtlSeconds();
    }

    public String validateRefreshAndGetUserId(String refreshJwt) {
        return jwtProvider.validateAndGetSubject(refreshJwt, JwtProvider.TokenType.REFRESH);
    }

    public String rotateRefresh(String oldRefresh) {
        String userId = jwtProvider.validateAndGetSubject(oldRefresh, JwtProvider.TokenType.REFRESH);
        return jwtProvider.generateRefresh(userId);
    }

    public void unlinkUser(Long userId) { // ← Long
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        kakao.unlinkWithAdminKey(user.getKakaoId());
        userRepository.deleteById(userId);
    }

    public record Tokens(String access, String refresh) {}
}
