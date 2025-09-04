package com.example.uni.user.dto;

import com.example.uni.user.domain.*;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class ProfileUpdateRequest {
    private String nickname;
    private Integer age;
    private String studentId;
    private String major;
    private Mbti mbti;
    private HeightBand heightBand;
    private String selfIntro;
    private MaleHair maleHair;
    private FemaleHair femaleHair;
}
