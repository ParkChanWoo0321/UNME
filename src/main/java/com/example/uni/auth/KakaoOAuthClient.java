package com.example.uni.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class KakaoOAuthClient {

    @Value("${kakao.auth.base-url}")
    private String authBase;
    @Value("${kakao.api.base-url}")
    private String apiBase;

    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.client-secret:}")
    private String clientSecret;

    @Value("${kakao.admin-key}")
    private String adminKey; // 🔹 unlink에 사용

    private WebClient webClient(String base) {
        return WebClient.builder().baseUrl(base).build();
    }

    public TokenResponse exchangeCodeForToken(String code, String redirectUri) {
        WebClient wc = webClient(authBase);
        MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("redirect_uri", redirectUri);
        form.add("code", code);
        if (!clientSecret.isBlank()) form.add("client_secret", clientSecret);

        return wc.post().uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }

    public KakaoUser me(String accessToken) {
        WebClient wc = webClient(apiBase);
        return wc.get().uri("/v2/user/me")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KakaoUser.class)
                .block();
    }

    // 🔹 Admin Key 기반 회원탈퇴 (서비스 JWT만으로 처리 가능)
    public void unlinkWithAdminKey(String kakaoId) {
        WebClient wc = webClient(apiBase);
        wc.post().uri("/v1/user/unlink")
                .header("Authorization", "KakaoAK " + adminKey)
                .bodyValue(Map.of("target_id_type", "user_id", "target_id", kakaoId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Data
    public static class TokenResponse {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private Long expires_in;
        private String scope;
        private Long refresh_token_expires_in;
    }

    @Data
    public static class KakaoUser {
        private Long id;
        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;

        @Data
        public static class KakaoAccount {
            @JsonProperty("has_email")
            private Boolean hasEmail;
            @JsonProperty("email_needs_agreement")
            private Boolean emailNeedsAgreement;
            private String email;

            @JsonProperty("profile")
            private Profile profile;

            @JsonProperty("has_gender")
            private Boolean hasGender;
            @JsonProperty("gender_needs_agreement")
            private Boolean genderNeedsAgreement;
            private String gender;

            @Data
            public static class Profile {
                private String nickname;
            }
        }
    }
}
