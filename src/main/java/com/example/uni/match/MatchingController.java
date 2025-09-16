package com.example.uni.match;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    private Long uid(String principal){ return Long.valueOf(principal); }

    /** 매칭 시작 */
    @PostMapping("/match/start")
    public ResponseEntity<MatchResultResponse> start(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.requestMatch(uid(principal)));
    }

    /** 신호 보내기 */
    @PostMapping("/signals/{targetId}")
    public ResponseEntity<Map<String,Object>> sendSignal(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable Long targetId){
        return ResponseEntity.ok(matchingService.sendSignal(uid(principal), targetId));
    }

    /** 신호 거절(받은 사람) */
    @PostMapping("/signals/decline/{signalId}")
    public ResponseEntity<Map<String,Object>> decline(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable Long signalId){
        return ResponseEntity.ok(matchingService.declineSignal(uid(principal), signalId));
    }

    /** 신호 수락 */
    @PostMapping("/signals/accept/{signalId}")
    public ResponseEntity<Map<String,Object>> accept(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable Long signalId){
        return ResponseEntity.ok(matchingService.acceptSignal(uid(principal), signalId));
    }

    /** 신호 보낸 목록 */
    @GetMapping("/signals/sent")
    public ResponseEntity<?> listSent(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listSentSignals(uid(principal)));
    }

    /** 신호 받은 목록 */
    @GetMapping("/signals/received")
    public ResponseEntity<?> listReceived(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listReceivedSignals(uid(principal)));
    }

    /** 매칭 성사 현황 */
    @GetMapping("/matches")
    public ResponseEntity<?> matches(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listMutualMatches(uid(principal)));
    }
}
