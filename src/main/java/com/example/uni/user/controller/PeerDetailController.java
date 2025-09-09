package com.example.uni.user.controller;

import com.example.uni.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PeerDetailController {

    private final UserService userService;

    /** 상대방 상세보기: 요약 포함 (로그인 필요) */
    @GetMapping("/{userId}/detail")
    public ResponseEntity<?> peerDetail(@PathVariable UUID userId) {
        var u = userService.get(userId);
        return ResponseEntity.ok(userService.toDetailCard(u));
    }
}