package com.example.uni.common.validation.validators;

import com.example.uni.common.validation.GenderConditional;
import com.example.uni.user.dto.ProfileOnboardingRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@GenderConditional
public class GenderConditionalValidator implements ConstraintValidator<GenderConditional, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!(value instanceof ProfileOnboardingRequest req)) return true;
        boolean hasMale = req.getMaleHair()!=null;
        boolean hasFemale = req.getFemaleHair()!=null;
        // 둘 다 채우거나 둘 다 비우면 실패
        if (hasMale == hasFemale) return false;
        // 나이/닉네임 등은 @NotNull/@NotBlank로 별도 검증
        return true;
    }
}
