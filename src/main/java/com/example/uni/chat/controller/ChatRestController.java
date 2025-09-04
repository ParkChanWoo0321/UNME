package com.example.uni.chat.controller;

import com.example.uni.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @PostMapping("/rooms/{peerId}")
    public ResponseEntity<?> createRoom(@org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
                                        @PathVariable UUID peerId){
        var room = chatService.createOrReuseRoom(UUID.fromString(principal), peerId);
        return ResponseEntity.ok(Map.of("roomId", room.getId()));
    }

    @PostMapping("/rooms/{roomId}/signal")
    public ResponseEntity<?> signal(@org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
                                    @PathVariable UUID roomId, @RequestParam String action){
        chatService.sendSignal(roomId, UUID.fromString(principal), action);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
