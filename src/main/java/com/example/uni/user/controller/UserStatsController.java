package com.example.uni.user.controller;

import com.example.uni.user.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService userStatsService;

    @GetMapping("/stats/egen-teto")
    public ResponseEntity<?> egenTeto() {
        return ResponseEntity.ok(userStatsService.egenTeto());
    }
}
