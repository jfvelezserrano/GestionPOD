package com.urjc.backend.error.exception;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class CSVValidationException extends RuntimeException{

    private final transient Set<ConstraintViolation<Object>> violations;

    public CSVValidationException(String message, Set<ConstraintViolation<Object>> violations) {
        super(message);
        this.violations = violations;
    }

    public Set<ConstraintViolation<Object>> getViolations() {
        return violations;
    }
}
