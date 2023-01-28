package com.urjc.backend.error.exception;

import org.springframework.http.HttpStatus;

public class RedirectException extends RuntimeException {

    private final HttpStatus status;

    public RedirectException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
