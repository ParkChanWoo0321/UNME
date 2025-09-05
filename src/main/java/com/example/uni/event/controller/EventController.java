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

    /** 코드 사용 → 매칭 크레딧 +3 */
    @PostMapping("/redeem")
    public ResponseEntity<Map<String,Object>> redeem(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @RequestParam String code){
        return ResponseEntity.ok(
                eventService.redeem(UUID.fromString(principal), code)
        );
    }
}
