package com.example.uni.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /** 코드 사용 → 매칭 크레딧 +3 (JSON Body) */
    @PostMapping(value = "/redeem", consumes = "application/json")
    public ResponseEntity<Map<String,Object>> redeem(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @Valid @RequestBody RedeemRequest body
    ){
        return ResponseEntity.ok(
                eventService.redeem(UUID.fromString(principal), body.getCode())
        );
    }
}