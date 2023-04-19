package com.urjc.backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = QuarterValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface QuarterConstraint {
    String message() default "El campo cuatrimestre es obligatorio y solo se admiten los siguientes valores: 'Primer Cuatrimestre' y 'Segundo Cuatrimestre'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
