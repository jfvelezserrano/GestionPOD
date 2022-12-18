package com.urjc.backend.error.exception;

import com.urjc.backend.model.Subject;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class CSVValidationException extends RuntimeException{
    private final transient Set<ConstraintViolation<Subject>> violations;

    public CSVValidationException(String message, Set<ConstraintViolation<Subject>> violations) {
        super(message);
        this.violations = violations;
    }

    public Set<ConstraintViolation<Subject>> getViolations() {
        return violations;
    }
}
