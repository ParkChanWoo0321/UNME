package com.example.uni.common.config;

import com.example.uni.auth.JwtProvider;
import com.example.uni.common.realtime.WsSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final WsSessionRegistry wsSessions;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(acc.getCommand())) {
            List<String> vals = new ArrayList<>();
            List<String> a1 = acc.getNativeHeader("Authorization");
            List<String> a2 = acc.getNativeHeader("authorization");
            if (a1 != null) vals.addAll(a1);
            if (a2 != null) vals.addAll(a2);
            String raw = vals.isEmpty() ? null : String.join(" ", vals).trim();
            if (raw == null || raw.length() < 7) throw new IllegalArgumentException("Missing Authorization");
            String prefix = raw.substring(0, Math.min(7, raw.length()));
            if (!prefix.equalsIgnoreCase("Bearer ")) throw new IllegalArgumentException("Bearer required");
            String jwt = raw.substring(7).replaceAll("\\s+", "");
            String userId = jwtProvider.validateAccessAndGetSubject(jwt);

            var principal = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            acc.setUser(principal);
            acc.setLeaveMutable(true);

            String sid = acc.getSessionId();
            if (sid != null) wsSessions.add(userId, sid);
            return message;
        }

        if (StompCommand.DISCONNECT.equals(acc.getCommand())) {
            var p = acc.getUser();
            if (p != null) {
                String userId = p.getName();
                String sid = acc.getSessionId();
                if (userId != null && sid != null) wsSessions.remove(userId, sid);
            }
            return message;
        }

        if (StompCommand.SUBSCRIBE.equals(acc.getCommand()) || StompCommand.SEND.equals(acc.getCommand())) {
            if (acc.getUser() == null) throw new IllegalArgumentException("Unauthenticated");
        }
        return message;
    }
}
