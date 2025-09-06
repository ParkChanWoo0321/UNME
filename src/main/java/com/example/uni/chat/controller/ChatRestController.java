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

    /** 방에 신호 전송: SEND | ACCEPT | DECLINE */
    @PostMapping("/rooms/{roomId}/signal")
    public ResponseEntity<?> signal(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable UUID roomId,
            @RequestParam String action
    ){
        chatService.sendSignal(roomId, UUID.fromString(principal), action);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /** 읽음 처리 */
    @PatchMapping("/rooms/{roomId}/read")
    public ResponseEntity<?> markRead(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable UUID roomId
    ){
        chatService.markRead(roomId, UUID.fromString(principal));
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /** 내가 속한 방 목록(요약) */
    @GetMapping("/rooms")
    public ResponseEntity<?> myRooms(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size
    ){
        var list = chatService.listMyRooms(UUID.fromString(principal), cursor, size);
        return ResponseEntity.ok(list);
    }

    /** 방 내 메시지 페이지 조회 */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> roomMessages(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable UUID roomId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") int size
    ){
        var page = chatService.listMessages(roomId, UUID.fromString(principal), cursor, size);
        return ResponseEntity.ok(page);
    }
}
