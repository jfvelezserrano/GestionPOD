package com.urjc.backend.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private int code;
    private HttpStatus status;
    private boolean redirect;
    private String message;
    private Map<String,String> errors;


    public ErrorResponse(HttpStatus status, String message, Map<String, String> errors) {
        super();
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.redirect = false;
        this.errors = errors;
    }

    public ErrorResponse(HttpStatus status, String message, boolean redirect) {
        super();
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.redirect = redirect;
        this.errors = Collections.emptyMap();
    }

    public ErrorResponse(HttpStatus status, String message) {
        super();
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.redirect = false;
        errors = Collections.emptyMap();
    }
}
