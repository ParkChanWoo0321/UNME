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

    @MessageMapping("/rooms/{roomId}/send")
    public void send(@DestinationVariable UUID roomId,
                     @Payload ChatSendPayload payload,
                     Principal principal) {

        String senderId = principal != null ? principal.getName() : "anonymous";

        messagingTemplate.convertAndSend("/topic/rooms/" + roomId,
                new ChatDelivered(senderId, payload.message()));
    }

    public record ChatSendPayload(String message) {}

    public record ChatDelivered(String senderId, String message) {}
}
