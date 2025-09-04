package com.example.uni.common.validation.validators;

import com.example.uni.common.validation.OppositeGenderIdeal;
import com.example.uni.user.dto.IdealOnboardingRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@OppositeGenderIdeal
public class OppositeGenderIdealValidator implements ConstraintValidator<OppositeGenderIdeal, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!(value instanceof IdealOnboardingRequest req)) return true;
        boolean hasMale = req.getIdealMaleHair()!=null;
        boolean hasFemale = req.getIdealFemaleHair()!=null;
        // 두 개 중 정확히 하나만 있어야 함
        return hasMale ^ hasFemale;
    }
}
