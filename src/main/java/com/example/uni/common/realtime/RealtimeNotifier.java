package com.example.uni.common.realtime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealtimeNotifier {

    public static final String Q_SIGNAL = "/queue/signal";
    public static final String Q_MATCH  = "/queue/match";

    private final SimpMessagingTemplate ws;

    /** 브라우저는 /user/queue/... 로만 구독. 내부에선 convertAndSendToUser로 단일 전송 */
    public void toUser(UUID userId, String dest, Object payload) {
        String pure = dest.startsWith("/user/") ? dest.substring(5) : dest;
        log.info("[WS] SEND user={} dest={}", userId, pure);
        ws.convertAndSendToUser(userId.toString(), pure, payload);
    }
}
