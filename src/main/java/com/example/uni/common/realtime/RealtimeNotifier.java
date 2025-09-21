package com.example.uni.common.realtime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealtimeNotifier {

    public static final String Q_SIGNAL = "/queue/signals";
    public static final String Q_MATCH  = "/queue/matches";

    private final SimpMessagingTemplate ws;

    /** /user/queue/... 로만 구독. 내부에선 convertAndSendToUser로 단일 전송 */
    public void toUser(Long userId, String dest, Object payload) { // ← Long로 변경
        String pure = dest.startsWith("/user/") ? dest.substring(5) : dest;
        String key = String.valueOf(userId);
        log.info("[WS] SEND user={} dest={}", key, pure);
        ws.convertAndSendToUser(key, pure, payload);
    }
}
