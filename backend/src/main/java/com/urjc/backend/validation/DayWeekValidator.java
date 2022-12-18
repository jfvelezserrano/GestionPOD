package com.urjc.backend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DayWeekValidator implements ConstraintValidator<DayWeekConstraint, Character> {

    @Override
    public boolean isValid(Character dayWeek, ConstraintValidatorContext cxt) {
        return dayWeek != null && (dayWeek.equals('L') || dayWeek.equals('M') || dayWeek.equals('X') || dayWeek.equals('J') || dayWeek.equals('V'));
    }

}
