package com.example.uni.common.validation;

import com.example.uni.common.validation.validators.OppositeGenderIdealValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OppositeGenderIdealValidator.class)
public @interface OppositeGenderIdeal {
    String message() default "invalid fields for opposite-gender ideal";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
