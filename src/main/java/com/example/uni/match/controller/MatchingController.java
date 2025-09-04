package com.example.uni.match.controller;

import com.example.uni.match.dto.MatchResultResponse;
import com.example.uni.match.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/request")
    public ResponseEntity<MatchResultResponse> request(@org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        return ResponseEntity.ok(matchingService.requestMatch(UUID.fromString(principal)));
    }
}
