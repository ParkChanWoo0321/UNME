package com.example.uni.common.config;

import com.example.uni.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization");
            if (auth == null) auth = accessor.getFirstNativeHeader("authorization");

            if (auth != null && auth.startsWith("Bearer ")) {
                String jwt = auth.substring(7);
                final String userId = jwtProvider.validateAndGetSubject(jwt);

                accessor.setUser(new Principal() {
                    @Override public String getName() { return userId; }
                });
            } else {
                throw new IllegalArgumentException("Missing Authorization header in STOMP CONNECT");
            }
        }
        return message;
    }
}
