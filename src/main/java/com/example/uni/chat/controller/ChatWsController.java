package com.example.uni.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final SimpMessagingTemplate messagingTemplate;

    // 앱에서 /app/rooms/{roomId}/send 로 publish
    @MessageMapping("/rooms/{roomId}/send")
    public void send(@DestinationVariable UUID roomId,
                     @Payload ChatSendPayload payload,
                     Principal principal) {

        // principal.getName() == STOMP CONNECT에서 검증한 JWT subject(= userId 문자열)
        String senderId = principal != null ? principal.getName() : "anonymous";

        // (선택) DB 저장/비즈니스 로직은 ChatService로 위임하면 좋음
        // chatService.saveAndFanout(roomId, UUID.fromString(senderId), payload.message());

        // 현재는 브로커로만 전파 (방 주제 구독자에게 전달)
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId,
                new ChatDelivered(senderId, payload.message()));
    }

    // 클라이언트가 보내는 페이로드(메시지 본문)
    public record ChatSendPayload(String message) {}

    // 구독자에게 전달되는 페이로드(보낸사람 포함)
    public record ChatDelivered(String senderId, String message) {}
}
