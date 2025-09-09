package com.example.uni.user.controller;

import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.DatingStyleRequest;
import com.example.uni.user.dto.ProfileOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.service.DatingStyleService;
import com.example.uni.user.service.LocalImageStorageService;
import com.example.uni.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LocalImageStorageService storage;
    private final DatingStyleService datingStyleService;

    private UUID uid(String principal){ return UUID.fromString(principal); }

    /** 닉네임 중복확인 */
    @GetMapping("/name/check")
    public ResponseEntity<?> checkName(@RequestParam("name") String name) {
        boolean available = userService.isNameAvailable(name);
        return ResponseEntity.ok(Map.of("available", available));
    }

    /** 프로필 온보딩(이름/학과/학번/출생연도) */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile(
            @AuthenticationPrincipal String principal,
            @Valid @RequestBody ProfileOnboardingRequest req
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

    /** 현재 프로필 조회 */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal String principal){
        User u = userService.get(uid(principal));
        return ResponseEntity.ok(userService.toResponse(u));
    }

    /** 데이팅 스타일 제출(가입 1회) */
    @PostMapping("/dating-style")
    public ResponseEntity<?> submitDatingStyle(
            @AuthenticationPrincipal String principal,
            @Validated @RequestBody DatingStyleRequest req
    ){
        return ResponseEntity.ok(datingStyleService.complete(uid(principal), req));
    }

    /** 내 상세보기(요약 포함) */
    @GetMapping("/detail")
    public ResponseEntity<?> myDetail(@AuthenticationPrincipal String principal){
        User u = userService.get(uid(principal));
        return ResponseEntity.ok(userService.toDetailCard(u));
    }

    /** 프로필 사진 업로드 */
    @PostMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> uploadPhoto(
            @AuthenticationPrincipal String principal,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        UUID me = uid(principal);
        User u = userService.get(me);
        String oldUrl = u.getProfileImageUrl();
        String newUrl = storage.storeProfileImage(me, file);
        u.setProfileImageUrl(newUrl);
        userService.save(u);
        if (oldUrl != null) storage.deleteByUrl(oldUrl);
        return ResponseEntity.ok(userService.toResponse(u));
    }

    /** 프로필 사진 삭제 */
    @DeleteMapping("/profile/photo")
    public ResponseEntity<?> deletePhoto(@AuthenticationPrincipal String principal) {
        UUID me = uid(principal);
        User u = userService.get(me);
        if (u.getProfileImageUrl() != null) {
            storage.deleteByUrl(u.getProfileImageUrl());
            u.setProfileImageUrl(null);
            userService.save(u);
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @Getter
    @Setter
    public static class GenderRequest {
        @NotNull
        private Gender gender;
    }
}