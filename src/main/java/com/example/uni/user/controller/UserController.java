package com.example.uni.user.controller;

import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.ProfileOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private UUID uid(String principal){ return UUID.fromString(principal); }

    /** 프로필 온보딩(이름/학과/학번/나이) */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile(
            @AuthenticationPrincipal String principal,
            @Validated @RequestBody ProfileOnboardingRequest req
    ){
        User u = userService.completeProfile(uid(principal), req);
        return ResponseEntity.ok(userService.toResponse(u));
    }

    /** 성별 1회 지정 */
    @PutMapping("/gender")
    public ResponseEntity<?> setGender(
            @AuthenticationPrincipal String principal,
            @Valid @RequestBody GenderRequest body
    ){
        userService.setGender(uid(principal), body.getGender());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /** 성향 저장(MBTI/태그 등) */
    @PutMapping("/traits")
    public ResponseEntity<?> saveTraits(
            @AuthenticationPrincipal String principal,
            @Valid @RequestBody TraitsRequest body
    ){
        userService.saveTraits(uid(principal), body);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /** 요약(온보딩 필요/크레딧) */
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

    // ===== DTO =====
    @Getter @Setter
    public static class GenderRequest {
        @NotNull private Gender gender;
    }
    @Getter @Setter
    public static class TraitsRequest {
        private String mbti;
        private List<String> tags;
        private Map<String,Object> extra; // 확장용
    }
}
