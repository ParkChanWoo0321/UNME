// src/main/java/com/example/uni/chat/controller/ChatWsController.java
package com.example.uni.chat.controller;

import com.example.uni.chat.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class ChatWsController {

    private final ChatService chatService;
    public ChatWsController(ChatService chatService) { this.chatService = chatService; }

    /** 클라이언트 → /app/rooms/{roomId}/send  (payload: {"content":"..."}  **/
    @MessageMapping("/rooms/{roomId}/send")
    public void send(@DestinationVariable UUID roomId,
                     @Valid @Payload ChatSendPayload payload,
                     Principal principal) {
        UUID me = UUID.fromString(principal.getName());
        chatService.sendText(roomId, me, payload.getContent());
    }

    @Setter @Getter @NoArgsConstructor
    public static class ChatSendPayload {
        @NotBlank
        private String content;
    }
}
