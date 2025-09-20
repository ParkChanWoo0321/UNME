// com/example/uni/auth/OAuthService.java
package com.example.uni.auth;

import com.example.uni.chat.ChatRoomService;
import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final KakaoOAuthClient kakao;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${app.unknown-user.name:알 수 없는 유저}")
    private String unknownUserName;
    @Value("${app.unknown-user.image:}")
    private String unknownUserImage;

    public Tokens loginWithAuthorizationCode(String code) {
        var token = kakao.exchangeCodeForToken(code, redirectUri);
        var me = kakao.me(token.getAccess_token());
        final String kakaoId = String.valueOf(me.getId());

        User existing = userRepository.findByKakaoId(kakaoId).orElse(null);

        if (existing != null && existing.getDeactivatedAt() != null) {
            existing.setName(null);
            if (existing.getKakaoId() != null && !existing.getKakaoId().startsWith("deleted:")) {
                existing.setKakaoId("deleted:" + existing.getKakaoId() + ":" + UUID.randomUUID());
            }
            existing.setEmail("deleted+" + existing.getId() + "+" + UUID.randomUUID() + "@deleted.local");
            userRepository.save(existing);
            existing = null;
        }

        final User user = (existing != null)
                ? existing
                : userRepository.save(
                User.builder()
                        .kakaoId(kakaoId)
                        .email(me.getKakaoAccount().getEmail())
                        .nickname(me.getKakaoAccount().getProfile().getNickname())
                        .profileComplete(false)
                        .matchCredits(0)
                        .build()
        );

        Long uid = user.getId();
        String access = jwtProvider.generateAccess(String.valueOf(uid));
        String refresh = jwtProvider.generateRefresh(String.valueOf(uid));
        return new Tokens(access, refresh);
    }

    public String issueAccessToken(String userId) {
        User u = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (u.getDeactivatedAt() != null) throw new ApiException(ErrorCode.FORBIDDEN);
        return jwtProvider.generateAccess(userId);
    }

    public long getAccessTtlSeconds() { return jwtProvider.getAccessTtlSeconds(); }

    public String validateRefreshAndGetUserId(String refreshJwt) {
        return jwtProvider.validateAndGetSubject(refreshJwt, JwtProvider.TokenType.REFRESH);
    }

    public String rotateRefresh(String oldRefresh) {
        String userId = jwtProvider.validateAndGetSubject(oldRefresh, JwtProvider.TokenType.REFRESH);
        User u = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (u.getDeactivatedAt() != null) throw new ApiException(ErrorCode.FORBIDDEN);
        return jwtProvider.generateRefresh(userId);
    }

    @Transactional
    public void unlinkUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String kid = user.getKakaoId();
        if (kid != null && !kid.startsWith("deleted:")) {
            kakao.unlinkWithAdminKey(kid);
        }

        user.setDeactivatedAt(LocalDateTime.now());
        user.setName(null);

        String suffix = "-" + user.getId() + "-" + UUID.randomUUID();
        if (kid != null && !kid.startsWith("deleted:")) {
            user.setKakaoId("deleted:" + kid + suffix);
        }
        user.setEmail("deleted+" + user.getId() + "+" + UUID.randomUUID() + "@deleted.local");
        user.setProfileImageUrl(null);

        // ✅ 이전 매칭 캐시 초기화
        user.setLastMatchJson("[]");
        user.setLastMatchAt(null);

        userRepository.save(user);
        chatRoomService.markUserLeft(userId, unknownUserName, unknownUserImage);
    }

    public record Tokens(String access, String refresh) {}
}
