// com/example/uni/common/config/WebSocketConfig.java
package com.example.uni.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Value("${ws.allowed-origins:*}")
    private String wsAllowedOrigins;

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        String[] patterns = Arrays.stream(wsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        if (patterns.length == 0) patterns = new String[] {"https://likelionhsu.co.kr"};

        registry.addEndpoint("/ws").setAllowedOriginPatterns(patterns);
        registry.addEndpoint("/ws").setAllowedOriginPatterns(patterns).withSockJS();

        registry.addEndpoint("/api/ws").setAllowedOriginPatterns(patterns);
        registry.addEndpoint("/api/ws").setAllowedOriginPatterns(patterns).withSockJS();
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
        registration.taskExecutor().corePoolSize(4).maxPoolSize(16).queueCapacity(1000);
    }

    @Override
    public void configureClientOutboundChannel(@NonNull ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4).maxPoolSize(16).queueCapacity(1000);
    }
}
