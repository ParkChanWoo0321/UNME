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

    public static final String Q_SIGNAL = "/queue/signal";           // 신호 이벤트
    public static final String Q_MATCH  = "/queue/match";            // 매칭 성사
    public static String qChat(UUID roomId) { return "/queue/chat/" + roomId; } // 방별 채팅
    public static final String Q_CHAT_LIST = "/queue/chat-list";     // 채팅목록 썸네일 갱신(선택)
}
