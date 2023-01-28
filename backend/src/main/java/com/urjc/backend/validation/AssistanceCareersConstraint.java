package com.urjc.backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = AssistanceCareersValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssistanceCareersConstraint {

    String message() default "Se admiten grupos de entre 1 y 255 letras. Los siguientes caracteres no están permitidos: []<>'\";!=";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}