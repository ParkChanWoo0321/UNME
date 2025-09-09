package com.example.uni.common.config;

import com.example.uni.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 클라이언트가 보낸 STOMP 네이티브 헤더 확인
            log.debug("STOMP CONNECT native headers = {}", accessor.toNativeHeaderMap());

            String auth = accessor.getFirstNativeHeader("Authorization");
            if (auth == null) auth = accessor.getFirstNativeHeader("authorization");
            if (auth != null) auth = auth.trim();

            if (auth != null && auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
                String jwt = auth.substring(7).trim();
                final String userId = jwtProvider.validateAndGetSubject(jwt);

                accessor.setUser(() -> userId);
                // ⬇️ 요기! setUser 직후에 연결 주체를 로그로
                log.debug("WS CONNECT as {}", userId);

            } else {
                throw new IllegalArgumentException("Missing Authorization header in STOMP CONNECT");
            }
        }
        return message;
    }
}