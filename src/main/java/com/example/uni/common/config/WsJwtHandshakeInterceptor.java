// com/example/uni/common/config/WsJwtHandshakeInterceptor.java
package com.example.uni.common.config;

import com.example.uni.auth.JwtProvider;
import com.example.uni.user.domain.User;
import com.example.uni.user.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class WsJwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest sreq) {
            HttpServletRequest req = sreq.getServletRequest();
            String token = req.getParameter("token");
            if (token != null && !token.isBlank()) {
                try {
                    String userId = jwtProvider.validateAccessAndGetSubject(token.trim());
                    User u = userRepository.findById(Long.valueOf(userId)).orElse(null);
                    if (u != null && u.getDeactivatedAt() == null) {
                        Principal p = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                        attributes.put("auth", p);
                    }
                } catch (Exception ignored) {}
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
}
