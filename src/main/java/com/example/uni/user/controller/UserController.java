// user/controller/UserController.java
package com.example.uni.user.controller;

import com.example.uni.user.domain.User;
import com.example.uni.user.dto.ProfileOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
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

    /** 프로필 온보딩(최초 입력: 이름/학과/학번/나이) */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile(
            @AuthenticationPrincipal String principal,
            @Validated @RequestBody ProfileOnboardingRequest req
    ){
        User u = userService.completeProfile(uid(principal), req);
        return ResponseEntity.ok(userService.toResponse(u));
    }

    /** 메인 요약(온보딩 필요/크레딧) */
    @GetMapping("/summary")
    public ResponseEntity<?> summary(@AuthenticationPrincipal String principal){
        User u = userService.get(uid(principal));
        return ResponseEntity.ok(Map.of(
                "onboardingRequired", !u.isProfileComplete(),
                "credits", u.getMatchCredits()
        ));
    }

    /** 현재 프로필 조회 */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal String principal){
        User u = userService.get(uid(principal));
        return ResponseEntity.ok(userService.toResponse(u));
    }

    // 이상형/성향 관련 엔드포인트 전면 제거
}
