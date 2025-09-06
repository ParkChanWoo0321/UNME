package com.example.uni.user.dto;

import com.example.uni.user.domain.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserProfileResponse {
    private UUID userId;
    private String name;
    private String department;
    private String studentNo;
    private Integer birthYear;   // 저장된 출생연도
    private Integer age;         // 응답 시 계산된 나이
    private Gender gender;
    private boolean profileComplete;
    private int matchCredits;
    private String profileImageUrl;
}