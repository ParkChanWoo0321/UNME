package com.example.uni.common.validation;

import com.example.uni.common.validation.validators.GenderConditionalValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderConditionalValidator.class)
public @interface GenderConditional {
    String message() default "invalid fields for gender";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}