package com.example.uni.user.dto;

import com.example.uni.user.domain.*;
import lombok.Builder; import lombok.Getter;

@Getter @Builder
public class UserProfileResponse {
    private String nickname;
    private Integer age;
    private String studentId;
    private String major;
    private Mbti mbti;
    private HeightBand heightBand;
    private String selfIntro;
    private MaleHair maleHair;
    private FemaleHair femaleHair;

    // ideal
    private Mbti idealMbti;
    private HeightBand idealHeightBand;
    private AgePref idealAgePref;
    private MaleHair idealMaleHair;
    private FemaleHair idealFemaleHair;

    private boolean profileComplete;
    private int matchCredits;
}
