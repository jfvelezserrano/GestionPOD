package com.urjc.backend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class QuarterValidator implements ConstraintValidator<QuarterConstraint, String> {

    @Override
    public boolean isValid(String quarter, ConstraintValidatorContext cxt) {
        return quarter != null && (quarter.equals("Primer Cuatrimestre") || quarter.equals("Segundo Cuatrimestre"));
    }
}
