package com.example.uni.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor; // ← 1) 인터셉터 주입

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // 개발단계: 넉넉히 허용(운영 땐 제한 권장)

        // SockJS를 쓰지 않으면 아래 블록은 제거해도 됩니다.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 대상
        registry.enableSimpleBroker("/topic", "/queue");
        // 앱에서 발행하는 prefix
        registry.setApplicationDestinationPrefixes("/app");
        // 1:1 사용자 큐 prefix (예: /user/queue/...)
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // ← 2) CONNECT 시 JWT 인증 수행
        registration.interceptors(stompAuthChannelInterceptor);
    }
}
