package com.example.uni.auth;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KakaoMobileLoginRequest {
    private String accessToken; // 앱이 카카오 SDK 로그인 후 넘겨줄 토큰
}