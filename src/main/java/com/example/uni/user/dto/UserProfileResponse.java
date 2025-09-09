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
    private Integer birthYear;
    private Integer age;
    private Gender gender;
    private boolean profileComplete;
    private int matchCredits;
    private String styleSummary;
}