package com.example.uni.common.config;

import com.example.uni.auth.JwtProvider;
import com.example.uni.common.realtime.WsSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final WsSessionRegistry wsSessions;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.debug("STOMP CONNECT native headers = {}", accessor.toNativeHeaderMap());

            String auth = accessor.getFirstNativeHeader("Authorization");
            if (auth == null) auth = accessor.getFirstNativeHeader("authorization");
            if (auth != null) auth = auth.trim();

            if (auth != null && auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
                String jwt = auth.substring(7).trim();
                final String userId = jwtProvider.validateAccessAndGetSubject(jwt);

                var principal = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                accessor.setUser(principal);
                accessor.setLeaveMutable(true);

                String sid = accessor.getSessionId();
                wsSessions.add(userId, sid);
                log.debug("WS CONNECT as {} (session={})", userId, sid);
            } else {
                throw new IllegalArgumentException("Missing Authorization header in STOMP CONNECT");
            }
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            var p = accessor.getUser();
            if (p != null) {
                String userId = p.getName();
                String sid = accessor.getSessionId();
                wsSessions.remove(userId, sid);
                log.debug("WS DISCONNECT {} (session={})", userId, sid);
            }
        }
        return message;
    }
}
