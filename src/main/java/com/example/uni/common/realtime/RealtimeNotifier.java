package com.example.uni.common.realtime;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RealtimeNotifier {
    private final SimpMessagingTemplate ws;

    // userId → /user/queue/{dest} 로 전송
    public void toUser(UUID userId, String dest, Object payload) {
        ws.convertAndSendToUser(userId.toString(), dest, payload);
    }

    // ⬇︎ 채팅 큐는 삭제, 신호/매칭만 유지
    public static final String Q_SIGNAL = "/queue/signal";  // 신호 이벤트
    public static final String Q_MATCH  = "/queue/match";   // 매칭 성사
}
