package com.example.uni.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (acc == null) return message;

        if (acc.getCommand() == StompCommand.CONNECT) {
            String auth = firstNativeHeaderIgnoreCase(acc, "Authorization");
            if (auth != null && auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
                String token = auth.substring(7).trim();
                String user = extractUserId(token);
                if (user != null && !user.isBlank()) {
                    acc.setUser(new UsernamePasswordAuthenticationToken(user, "N/A", Collections.emptyList()));
                }
            }
        }

        if ((acc.getCommand() == StompCommand.SUBSCRIBE || acc.getCommand() == StompCommand.SEND) && acc.getUser() == null) {
            throw new AccessDeniedException("Unauthenticated STOMP");
        }

        return message;
    }

    private static String firstNativeHeaderIgnoreCase(StompHeaderAccessor acc, String name) {
        List<String> v = acc.getNativeHeader(name);
        if (v != null && !v.isEmpty()) return v.get(0);
        for (Map.Entry<String, List<String>> e : acc.toNativeHeaderMap().entrySet()) {
            if (e.getKey() != null && e.getKey().equalsIgnoreCase(name) && e.getValue() != null && !e.getValue().isEmpty()) {
                return e.getValue().get(0);
            }
        }
        return null;
    }

    private static String extractUserId(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;
            String p = parts[1].replace('-', '+').replace('_', '/');
            int pad = (4 - (p.length() % 4)) % 4;
            p = p + "====".substring(0, pad);
            String json = new String(Base64.getDecoder().decode(p), StandardCharsets.UTF_8);
            Map<String, Object> m = new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>(){});
            Object v = m.getOrDefault("userId", m.getOrDefault("id", m.get("sub")));
            return v == null ? null : String.valueOf(v);
        } catch (Exception e) {
            return null;
        }
    }
}