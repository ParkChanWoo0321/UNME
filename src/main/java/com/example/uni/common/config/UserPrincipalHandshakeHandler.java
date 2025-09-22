// com/example/uni/common/config/UserPrincipalHandshakeHandler.java
package com.example.uni.common.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class UserPrincipalHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Object p = attributes.get("auth");
        return p instanceof Principal ? (Principal) p : super.determineUser(request, wsHandler, attributes);
    }
}
