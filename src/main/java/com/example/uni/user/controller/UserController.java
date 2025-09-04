package com.example.uni.user.controller;

import com.example.uni.user.domain.User;
import com.example.uni.user.dto.*;
import com.example.uni.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private UUID uid(String principal){ return UUID.fromString(principal); }

    @PutMapping("/profile")
    public ResponseEntity<?> profile(
            @AuthenticationPrincipal String principal,
            @Validated @RequestBody ProfileOnboardingRequest req
    ){
        User u = userService.completeProfile(UUID.fromString(principal), req);
        return ResponseEntity.ok(userService.toResponse(u));
    }

    @PutMapping("/ideal")
    public ResponseEntity<?> ideal(@org.springframework.security.core.annotation.AuthenticationPrincipal String principal,
                                   @Validated @RequestBody IdealOnboardingRequest req){
        User u = userService.completeIdeal(uid(principal), req);
        return ResponseEntity.ok(Map.of(
                "profileComplete", u.isProfileComplete()
        ));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@org.springframework.security.core.annotation.AuthenticationPrincipal String principal){
        User u = userService.get(uid(principal));
        return ResponseEntity.ok(Map.of(
                "onboardingRequired", !u.isProfileComplete(),
                "credits", u.getMatchCredits()
        ));
    }
}
