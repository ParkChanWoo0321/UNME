package com.example.uni.user.controller;

import com.example.uni.user.domain.User;
import com.example.uni.user.dto.UserOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private Long uid(String principal){ return Long.valueOf(principal); } // ← Long

    /** 닉네임 중복확인 */
    @GetMapping("/name/check")
    public ResponseEntity<?> checkName(@RequestParam("name") String name) {
        boolean available = userService.isNameAvailable(name);
        String message = available ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
        return ResponseEntity.ok(Map.of(
                "available", available,
                "message", message
        ));
    }

    /** 내 정보 입력 통합 (기본정보 + 성별 + 성향테스트) */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile(
            @AuthenticationPrincipal String principal,
            @Valid @RequestBody UserOnboardingRequest req
    ){
        User u = userService.completeProfile(uid(principal), req); // ← Long
        return ResponseEntity.ok(userService.toResponse(u));
    }

    /** 현재 프로필 조회 */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal String principal){
        User u = userService.get(uid(principal)); // ← Long
        return ResponseEntity.ok(userService.toResponse(u));
    }

    /** 한 줄 소개 작성/수정 */
    @PutMapping("/introduce")
    public ResponseEntity<UserProfileResponse> updateIntroduce(
            @AuthenticationPrincipal String principal,
            @RequestBody Map<String,String> body
    ){
        String introduce = body.get("introduce");
        User u = userService.updateIntroduce(uid(principal), introduce); // ← Long
        return ResponseEntity.ok(userService.toResponse(u));
    }

    /** 인스타 아이디(또는 URL) 작성/수정 → 저장은 URL로 */
    @PutMapping("/instagram")
    public ResponseEntity<UserProfileResponse> updateInstagram(
            @AuthenticationPrincipal String principal,
            @RequestBody Map<String,String> body
    ){
        String raw = body.getOrDefault("instagram", body.get("instagramId"));
        User u = userService.updateInstagram(uid(principal), raw); // ← Long
        return ResponseEntity.ok(userService.toResponse(u));
    }
}
