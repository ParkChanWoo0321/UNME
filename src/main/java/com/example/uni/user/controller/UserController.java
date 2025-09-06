// src/main/java/com/example/uni/user/controller/UserController.java
package com.example.uni.user.controller;

import com.example.uni.user.domain.Gender;
import com.example.uni.user.domain.User;
import com.example.uni.user.dto.ProfileOnboardingRequest;
import com.example.uni.user.dto.UserProfileResponse;
import com.example.uni.user.service.LocalImageStorageService;
import com.example.uni.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType; // ⬅ 추가
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // ⬅ 추가

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LocalImageStorageService storage; // ⬅ 추가

    private UUID uid(String principal){ return UUID.fromString(principal); }

    /** 프로필 온보딩(이름/학과/학번/출생연도) */
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

    /** 프로필 사진 업로드 */
    @PostMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> uploadPhoto(
            @AuthenticationPrincipal String principal,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        UUID me = uid(principal);
        User u = userService.get(me);

        // 기존 파일 있으면 삭제(선택)
        if (u.getProfileImageUrl() != null) storage.deleteByUrl(u.getProfileImageUrl());

        String url = storage.storeProfileImage(me, file);
        u.setProfileImageUrl(url);
        userService.save(u); // ⬅ 저장

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
