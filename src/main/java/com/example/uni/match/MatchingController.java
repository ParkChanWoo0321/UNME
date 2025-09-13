package com.example.uni.match;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    /** 매칭 시작 */
    @PostMapping("/match/start")
    public ResponseEntity<MatchResultResponse> start(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.requestMatch(UUID.fromString(principal)));
    }

    /** 신호 보내기 */
    @PostMapping("/signals/{targetId}")
    public ResponseEntity<Map<String,Object>> sendSignal(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable UUID targetId){
        return ResponseEntity.ok(matchingService.sendSignal(UUID.fromString(principal), targetId));
    }

    /** 신호 취소(보낸 사람) */
    @DeleteMapping("/signals/{signalId}")
    public ResponseEntity<Map<String,Object>> cancel(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable UUID signalId){
        return ResponseEntity.ok(matchingService.cancelSignal(UUID.fromString(principal), signalId));
    }

    /** 신호 거절(받은 사람) */
    @PostMapping("/signals/{signalId}/decline")
    public ResponseEntity<Map<String,Object>> decline(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable UUID signalId){
        return ResponseEntity.ok(matchingService.declineSignal(UUID.fromString(principal), signalId));
    }

    /** 신호 수락 */
    @PostMapping("/signals/{signalId}/accept")
    public ResponseEntity<Map<String,Object>> accept(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable UUID signalId){
        return ResponseEntity.ok(matchingService.acceptSignal(UUID.fromString(principal), signalId));
    }

    /** 신호 보낸 목록 */
    @GetMapping("/signals/sent")
    public ResponseEntity<?> listSent(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listSentSignals(UUID.fromString(principal)));
    }

    /** 신호 받은 목록 */
    @GetMapping("/signals/received")
    public ResponseEntity<?> listReceived(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listReceivedSignals(UUID.fromString(principal)));
    }

    /** 매칭 성사 현황 */
    @GetMapping("/matches")
    public ResponseEntity<?> matches(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listMutualMatches(UUID.fromString(principal)));
    }
}
