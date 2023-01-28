package com.urjc.backend.error.exception;

import org.springframework.http.HttpStatus;

public class GlobalException extends RuntimeException {

    private final HttpStatus status;

    public GlobalException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
