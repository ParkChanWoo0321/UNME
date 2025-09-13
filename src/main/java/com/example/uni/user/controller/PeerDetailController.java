package com.example.uni.user.controller;

import com.example.uni.user.domain.User;
import com.example.uni.user.dto.PeerDetailResponse;
import com.example.uni.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PeerDetailController {

    private final UserService userService;

    /** 상대방 상세보기 (introduce 포함, 로그인 필요) */
    @GetMapping("/{userId}/detail")
    public ResponseEntity<PeerDetailResponse> peerDetail(@PathVariable UUID userId) {
        User u = userService.get(userId);
        return ResponseEntity.ok(userService.toPeerResponse(u));
    }
}
