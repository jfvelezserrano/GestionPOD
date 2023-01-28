package com.urjc.backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TurnValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TurnConstraint {
    String message() default "El campo turno es obligatorio y solo se admiten los siguientes caracteres: 'M' y 'T'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
