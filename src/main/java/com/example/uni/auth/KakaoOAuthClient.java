package com.example.uni.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

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

    public TokenResponse exchangeCodeForToken(String code, String redirectUri) {
        WebClient wc = WebClient.builder().baseUrl(authBase).build();
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
        WebClient wc = WebClient.builder().baseUrl(apiBase).build();
        return wc.get().uri("/v2/user/me")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KakaoUser.class)
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
            @JsonProperty("has_gender")
            private Boolean hasGender;
            @JsonProperty("gender_needs_agreement")
            private Boolean genderNeedsAgreement;
            private String gender;
        }
    }
}