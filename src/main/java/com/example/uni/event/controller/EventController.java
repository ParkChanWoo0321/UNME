package com.example.uni.event.controller;

import com.example.uni.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 부스 운영자 발급(실서비스에선 ROLE 체크 필요)
    @PostMapping("/verify-codes")
    public ResponseEntity<?> issue(@RequestParam(defaultValue="15") int ttlMinutes){
        String code = eventService.issueCode(ttlMinutes);
        return ResponseEntity.ok(Map.of("code", code));
    }

    // 유저가 입력
    @PostMapping("/verify-codes/{code}/use")
    public ResponseEntity<?> use(@org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
                                 @PathVariable String code){
        int credits = eventService.useCode(UUID.fromString(principal), code);
        return ResponseEntity.ok(Map.of("creditsAfter", credits));
    }
}
