package com.example.uni.user.dto;

import com.example.uni.user.domain.*;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class IdealUpdateRequest {
    private Mbti idealMbti;
    private HeightBand idealHeightBand;
    private AgePref idealAgePref;
    private MaleHair idealMaleHair;
    private FemaleHair idealFemaleHair;
}
