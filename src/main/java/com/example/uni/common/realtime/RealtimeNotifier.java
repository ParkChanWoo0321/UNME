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

    /** /user 접두어가 넘어와도 정규화하고, 세션 목적지로 직통 전송 */
    public void toUser(UUID userId, String dest, Object payload) {
        String pure = dest.startsWith("/user/") ? dest.substring(5) : dest;

        Set<String> sids = wsSessions.sessions(userId.toString());
        log.info("[WS] SEND user={} dest={} sessionCount={} sessions={}",
                userId, pure, sids.size(), sids);

        if (sids.isEmpty()) {
            // fallback: 내부 레지스트리에 늦게 뜨는 경우라도 한번 시도
            ws.convertAndSendToUser(userId.toString(), pure, payload);
            return;
        }

        // 세션별 목적지 (/queue/xxx-user<sessionId>)로 직접 전송
        for (String sid : sids) {
            String sessionDest = pure + "-user" + sid;
            ws.convertAndSend(sessionDest, payload);
        }
    }

    public void toUserSignal(UUID userId, Object payload) { toUser(userId, Q_SIGNAL, payload); }
    public void toUserMatch (UUID userId, Object payload) { toUser(userId, Q_MATCH , payload); }
}
