// com/example/uni/user/controller/UserController.java
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

    private Long uid(String principal){ return Long.valueOf(principal); }

    @GetMapping("/name/check")
    public ResponseEntity<?> checkName(@RequestParam("name") String name) {
        boolean available = userService.isNameAvailable(name);
        String message = available ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
        return ResponseEntity.ok(Map.of("available", available, "message", message));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile(
            @AuthenticationPrincipal String principal,
            @Valid @RequestBody UserOnboardingRequest req
    ){
        User u = userService.completeProfile(uid(principal), req);
        return ResponseEntity.ok(userService.toResponse(u));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal String principal){
        User u = userService.get(uid(principal));
        return ResponseEntity.ok(userService.toResponse(u));
    }

    @PutMapping("/introduce")
    public ResponseEntity<UserProfileResponse> updateIntroduce(
            @AuthenticationPrincipal String principal,
            @RequestBody Map<String,String> body
    ){
        String introduce = body.get("introduce");
        User u = userService.updateIntroduce(uid(principal), introduce);
        return ResponseEntity.ok(userService.toResponse(u));
    }

    @PutMapping("/instagram")
    public ResponseEntity<UserProfileResponse> updateInstagram(
            @AuthenticationPrincipal String principal,
            @RequestBody Map<String,String> body
    ){
        String raw = body.getOrDefault("instagram", body.get("instagramId"));
        User u = userService.updateInstagram(uid(principal), raw);
        return ResponseEntity.ok(userService.toResponse(u));
    }

    @PutMapping("/profile-image")
    public ResponseEntity<UserProfileResponse> updateProfileImage(
            @AuthenticationPrincipal String principal,
            @RequestBody Map<String,String> body
    ){
        String imageUrl = body.get("imageUrl");
        User u = userService.updateProfileImageUrl(uid(principal), imageUrl);
        return ResponseEntity.ok(userService.toResponse(u));
    }
}
