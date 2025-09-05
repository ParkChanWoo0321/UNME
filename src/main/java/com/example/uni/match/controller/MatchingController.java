// match/controller/MatchingController.java
package com.example.uni.match.controller;

import com.example.uni.match.dto.MatchResultResponse;
import com.example.uni.match.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    /** 매칭 시작(크레딧 1 차감, 랜덤 3명) */
    @PostMapping("/match/start")
    public ResponseEntity<MatchResultResponse> start(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.requestMatch(UUID.fromString(principal)));
    }

    /** 신호 보내기(멱등) */
    @PostMapping("/signals/{targetId}")
    public ResponseEntity<Map<String,Object>> sendSignal(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable UUID targetId){
        return ResponseEntity.ok(matchingService.sendSignal(UUID.fromString(principal), targetId));
    }

    /** 내가 보낸 신호 목록 */
    @GetMapping("/signals/sent")
    public ResponseEntity<?> listSent(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listSentSignals(UUID.fromString(principal)));
    }

    /** 내가 받은 신호 목록 */
    @GetMapping("/signals/received")
    public ResponseEntity<?> listReceived(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listReceivedSignals(UUID.fromString(principal)));
    }

    /** 신호 수락(상호 성사→채팅방 생성) */
    @PostMapping("/signals/{signalId}/accept")
    public ResponseEntity<Map<String,Object>> accept(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable UUID signalId){
        return ResponseEntity.ok(matchingService.acceptSignal(UUID.fromString(principal), signalId));
    }

    /** 매칭 성사 현황(채팅 단계) */
    @GetMapping("/matches")
    public ResponseEntity<?> matches(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listMutualMatches(UUID.fromString(principal)));
    }
}
