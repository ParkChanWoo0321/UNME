package com.example.uni.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileOnboardingRequest {

    @NotBlank(message = "닉네임은 2~8자입니다")
    @Pattern(regexp = "^.{2,}$",  message = "닉네임은 최소 2자 이상이어야 합니다")
    @Pattern(regexp = "^.{0,8}$", message = "닉네임은 최대 8자까지 가능합니다")
    private String name;

    @NotBlank(message = "학과는 필수입니다")
    private String department;

    @NotBlank(message = "학번은 필수입니다")
    @Pattern(regexp = "^(1[5-9]|2[0-9])$", message = "학번은 15 이상이어야 합니다")
    @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-5])$", message = "학번은 25 이하여야 합니다")
    private String studentNo;

    @NotNull(message = "출생연도는 1990~2006입니다")
    @Min(value = 1990, message = "출생연도는 1990 이상이어야 합니다")
    @Max(value = 2006, message = "출생연도는 2006 이하여야 합니다")
    private Integer birthYear;
}