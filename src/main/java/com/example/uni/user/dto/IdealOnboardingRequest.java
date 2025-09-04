package com.example.uni.user.dto;

import com.example.uni.common.validation.OppositeGenderIdeal;
import com.example.uni.user.domain.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
@OppositeGenderIdeal
public class IdealOnboardingRequest {
    @NotNull private Mbti idealMbti;
    @NotNull private HeightBand idealHeightBand;
    @NotNull private AgePref idealAgePref;

    private MaleHair idealMaleHair;       // 본인이 FEMALE일 때 필수
    private FemaleHair idealFemaleHair;   // 본인이 MALE일 때 필수
}
