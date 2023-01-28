package com.urjc.backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DayWeekValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DayWeekConstraint {
    String message() default "Día de la semana incorrecto, solo se admiten los siguientes caracteres: 'L', 'M', 'X', 'J' y 'V'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
