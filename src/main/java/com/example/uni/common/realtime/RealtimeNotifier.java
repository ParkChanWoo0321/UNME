package com.example.uni.common.realtime;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RealtimeNotifier {
    private final SimpMessagingTemplate ws;

    public void toUser(UUID userId, String dest, Object payload) {
        ws.convertAndSendToUser(userId.toString(), dest, payload);
    }

    public static final String Q_SIGNAL = "/queue/signal";
    public static final String Q_MATCH  = "/queue/match";
}
