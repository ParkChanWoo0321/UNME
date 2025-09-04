package com.example.uni.auth;

import com.example.uni.common.exception.ApiException;
import com.example.uni.common.exception.ErrorCode;
import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoMobileLoginService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Qualifier("kakaoRestClient")
    private final RestClient kakaoRestClient;

    @Value("${kakao.api.userinfo-path}")
    private String userinfoPath;

    @Transactional
    public String loginWithKakaoAccessToken(String kakaoAccessToken) {
        Map<String, Object> profile;
        try {
            profile = kakaoRestClient.get()
                    .uri(userinfoPath)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 401 || e.getRawStatusCode() == 403) {
                throw new ApiException(ErrorCode.UNAUTHORIZED);
            }
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (profile == null || !profile.containsKey("id")) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        String kakaoId = String.valueOf(profile.get("id"));

        String email = null;
        String genderStr = null;
        Object accObj = profile.get("kakao_account");
        if (accObj instanceof Map<?, ?> acc) {
            Object emailObj = acc.get("email");
            if (emailObj instanceof String s) email = s;
            Object genderObj = acc.get("gender");
            if (genderObj instanceof String s) genderStr = s;
        }

        Gender gender = null;
        if ("male".equalsIgnoreCase(genderStr))      gender = Gender.MALE;
        else if ("female".equalsIgnoreCase(genderStr)) gender = Gender.FEMALE;

        final String emailFinal = email;
        final Gender genderFinal = gender;

        User user = userRepository.findByKakaoId(kakaoId).orElseGet(() ->
                userRepository.save(User.builder()
                        .kakaoId(kakaoId)
                        .email(emailFinal)
                        .gender(genderFinal)      // null 허용
                        .profileComplete(false)
                        .matchCredits(1)
                        .build())
        );

        if (user.getEmail() == null && emailFinal != null)   user.setEmail(emailFinal);
        if (user.getGender() == null && genderFinal != null) user.setGender(genderFinal);

        return jwtProvider.generate(user.getId().toString(), kakaoId);
    }
}
