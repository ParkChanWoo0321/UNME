// com/example/uni/match/MatchingController.java
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

    @GetMapping("/match/previous")
    public ResponseEntity<?> previous(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.previousMatches(uid(principal)));
    }

    @GetMapping("/signals/{targetId}/status")
    public ResponseEntity<Map<String,Object>> signalStatus(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable Long targetId){
        return ResponseEntity.ok(matchingService.signalStatus(uid(principal), targetId));
    }

    @PostMapping("/match/start")
    public ResponseEntity<MatchResultResponse> start(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.requestMatch(uid(principal)));
    }

    @PostMapping("/signals/{targetId}")
    public ResponseEntity<Map<String,Object>> sendSignal(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable Long targetId){
        return ResponseEntity.ok(matchingService.sendSignal(uid(principal), targetId));
    }

    @PostMapping("/signals/decline/{signalId}")
    public ResponseEntity<Map<String,Object>> decline(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable Long signalId){
        return ResponseEntity.ok(matchingService.declineSignal(uid(principal), signalId));
    }

    @PostMapping("/signals/accept/{signalId}")
    public ResponseEntity<Map<String,Object>> accept(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
            @PathVariable Long signalId){
        return ResponseEntity.ok(matchingService.acceptSignal(uid(principal), signalId));
    }

    @GetMapping("/signals/sent")
    public ResponseEntity<?> listSent(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listSentSignals(uid(principal)));
    }

    @GetMapping("/signals/received")
    public ResponseEntity<?> listReceived(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.listReceivedSignals(uid(principal)));
    }
}
