// com/example/uni/common/config/StompAuthChannelInterceptor.java
package com.example.uni.common.config;

import com.example.uni.auth.JwtProvider;
import com.example.uni.common.realtime.WsSessionRegistry;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final WsSessionRegistry wsSessions;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(acc.getCommand())) {
            String raw = Optional.ofNullable(acc.getFirstNativeHeader("Authorization"))
                    .orElse(acc.getFirstNativeHeader("authorization"));
            if (raw == null) throw new AccessDeniedException("Missing Authorization");
            raw = raw.trim();
            if (raw.length() < 7 || !raw.regionMatches(true, 0, "Bearer ", 0, 7)) {
                throw new AccessDeniedException("Bearer required");
            }

            String jwt = raw.substring(7).trim();
            final String userId;
            try {
                userId = jwtProvider.validateAccessAndGetSubject(jwt);
            } catch (RuntimeException e) {
                throw new AccessDeniedException("Invalid or expired token", e);
            }

            User u = userRepository.findById(Long.valueOf(userId)).orElse(null);
            if (u == null || u.getDeactivatedAt() != null) {
                throw new AccessDeniedException("Deactivated");
            }

            var principal = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            acc.setUser(principal);
            acc.setLeaveMutable(true);

            String sid = acc.getSessionId();
            if (sid != null) wsSessions.add(userId, sid);

            return MessageBuilder.createMessage(message.getPayload(), acc.getMessageHeaders());
        }

        if (StompCommand.DISCONNECT.equals(acc.getCommand())) {
            var p = acc.getUser();
            if (p != null) {
                String userId = p.getName();
                String sid = acc.getSessionId();
                if (userId != null && sid != null) wsSessions.remove(userId, sid);
            }
            return MessageBuilder.createMessage(message.getPayload(), acc.getMessageHeaders());
        }

        if (StompCommand.SUBSCRIBE.equals(acc.getCommand()) || StompCommand.SEND.equals(acc.getCommand())) {
            if (acc.getUser() == null) throw new AccessDeniedException("Unauthenticated");
        }
        return message;
    }
}
