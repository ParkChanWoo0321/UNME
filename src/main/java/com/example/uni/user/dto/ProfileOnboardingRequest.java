package com.example.uni.user.dto;

import com.example.uni.common.validation.GenderConditional;
import com.example.uni.user.domain.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
@GenderConditional
public class ProfileOnboardingRequest {
    @NotBlank private String nickname;
    @NotNull  private Integer age;
    @NotBlank private String studentId;
    @NotBlank private String major;

    @NotNull  private Mbti mbti;
    @NotNull  private HeightBand heightBand;
    @NotBlank private String selfIntro;

    private MaleHair maleHair;        // 남자면 필수
    private FemaleHair femaleHair;    // 여자면 필수
}
