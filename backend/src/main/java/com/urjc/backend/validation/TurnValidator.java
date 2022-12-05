package com.urjc.backend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TurnValidator implements ConstraintValidator<TurnConstraint, Character> {

    @Override
    public void initialize(TurnConstraint turnConstraint) {
    }

    @Override
    public boolean isValid(Character turn, ConstraintValidatorContext cxt) {
        return turn != null && (turn.equals('M') || turn.equals('T'));
    }
}
