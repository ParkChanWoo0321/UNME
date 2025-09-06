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

    public String loginWithAuthorizationCode(String code) {
        var token = kakao.exchangeCodeForToken(code, redirectUri);
        var me = kakao.me(token.getAccess_token());

        final String kakaoId = String.valueOf(me.getId());

        // 1) 성별 확정(람다에서 캡처할 final 값)
        final Gender resolvedGender = resolveGender(me);

        // 2) upsert (기존 유저도 값 갱신)
        User user = userRepository.findByKakaoId(kakaoId)
                .map(u -> {
                    if (resolvedGender != null && u.getGender() == null) {
                        u.setGender(resolvedGender);
                    }
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
        return jwtProvider.generateToken(uid.toString());
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
        // acc.getGenderNeedsAgreement() == true 인 케이스는 재동의 유도 로직을
        // 필요 시 별도 처리(예외)로 분기하면 됨.
    }
}
