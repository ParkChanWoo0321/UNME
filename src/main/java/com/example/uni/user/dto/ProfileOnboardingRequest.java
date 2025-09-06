package com.example.uni.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileOnboardingRequest {

    @NotBlank @Size(min = 2, max = 8)
    private String name;        // 닉네임(2~8)

    @NotBlank
    private String department;  // 학과(프론트에서 선택하여 텍스트로 전달)

    @NotBlank @Size(min = 15, max = 25)
    private String studentNo;   // 학번(15~25)

    @NotNull @Min(1990) @Max(2006)
    private Integer birthYear;  // 출생연도(1990~2006)
}
