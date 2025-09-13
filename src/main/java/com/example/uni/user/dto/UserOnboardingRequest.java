package com.example.uni.user.dto;

import com.example.uni.user.domain.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOnboardingRequest {

    @NotBlank(message = "닉네임은 2~8자입니다")
    @Pattern(regexp = "^.{2,}$",  message = "최소 두글자 이상 지어주세요.")
    @Pattern(regexp = "^.{0,8}$", message = "닉네임은 최대 8자까지 가능합니다")
    private String name;

    @NotBlank(message = "학과를 선택하세요.")
    private String department;

    @NotBlank(message = "학번을 확인해주세요.")
    private String studentNo;

    // 두 자리: 90~99, 00~06
    @NotBlank(message = "년생을 입력하세요.")
    @Pattern(regexp = "^(9\\d|0[0-6])$", message = "나이를 확인해주세요.")
    private String birthYear;

    @NotNull(message = "성별은 필수입니다")
    private Gender gender;

    @NotNull @Pattern(regexp="^[ab]$") private String q1;
    @NotNull @Pattern(regexp="^[ab]$") private String q2;
    @NotNull @Pattern(regexp="^[ab]$") private String q3;
    @NotNull @Pattern(regexp="^[ab]$") private String q4;
    @NotNull @Pattern(regexp="^[ab]$") private String q5;
    @NotNull @Pattern(regexp="^[ab]$") private String q6;
    @NotNull @Pattern(regexp="^[ab]$") private String q7;
    @NotNull @Pattern(regexp="^[ab]$") private String q8;
    @NotNull @Pattern(regexp="^[ab]$") private String q9;
    @NotNull @Pattern(regexp="^[ab]$") private String q10;
}