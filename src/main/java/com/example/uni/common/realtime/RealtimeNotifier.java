package com.example.uni.common.realtime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealtimeNotifier {

    public static final String Q_SIGNAL = "/queue/signal";
    public static final String Q_MATCH  = "/queue/match";

    private final SimpMessagingTemplate ws;
    private final WsSessionRegistry wsSessions;

    public void toUser(UUID userId, String dest, Object payload) {
        String pure = dest.startsWith("/user/") ? dest.substring(5) : dest;

        Set<String> sids = wsSessions.sessions(userId.toString());
        log.info("[WS] SEND user={} dest={} sessionCount={} sessions={}",
                userId, pure, sids.size(), sids);

        if (sids.isEmpty()) {
            ws.convertAndSendToUser(userId.toString(), pure, payload);
            return;
        }
        for (String sid : sids) {
            String sessionDest = pure + "-user" + sid;
            ws.convertAndSend(sessionDest, payload);
        }
    }
}