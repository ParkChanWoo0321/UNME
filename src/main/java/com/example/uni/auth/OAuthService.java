package com.example.uni.auth;

import com.example.uni.chat.ChatRoomService;
import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import com.example.uni.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final KakaoOAuthClient kakao;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("#{T(org.springframework.util.StringUtils).hasText('${app.unknown-user.name:}') ? '${app.unknown-user.name}' : '탈퇴한 사용자'}")
    private String unknownUserName;

    public Tokens loginWithAuthorizationCode(String code) {
        var token = kakao.exchangeCodeForToken(code, redirectUri);
        var me = kakao.me(token.getAccess_token());
        String kakaoId = String.valueOf(me.getId());
        String email = me.getKakaoAccount().getEmail();

        User user = userRepository.findByKakaoId(kakaoId).orElse(null);
        if (user != null) {
            if (user.getDeactivatedAt() != null) throw new ApiException(ErrorCode.ACCOUNT_DEACTIVATED);
        } else {
            Optional<User> byEmail = userRepository.findByEmail(email);
            if (byEmail.isPresent()) {
                if (byEmail.get().getDeactivatedAt() != null) throw new ApiException(ErrorCode.ACCOUNT_DEACTIVATED);
                user = byEmail.get();
            } else {
                user = userRepository.save(
                        User.builder()
                                .kakaoId(kakaoId)
                                .email(email)
                                .nickname(me.getKakaoAccount().getProfile().getNickname())
                                .profileComplete(false)
                                .matchCredits(0)
                                .build()
                );
            }
        }

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
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        String kid = user.getKakaoId();
        if (kid != null) kakao.unlinkWithAdminKey(kid);
        user.setDeactivatedAt(LocalDateTime.now());
        user.setName(null);
        user.setProfileImageUrl(null);
        user.setLastMatchJson("[]");
        user.setLastMatchAt(null);
        userRepository.save(user);
        int typeId = user.getTypeId() != null ? user.getTypeId() : 4;
        String leftImg = userService.resolveTypeImage3(typeId);
        chatRoomService.markUserLeft(userId, unknownUserName, leftImg);
    }

    public record Tokens(String access, String refresh) {}
}
