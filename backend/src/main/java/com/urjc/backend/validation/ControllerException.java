package com.urjc.backend.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerException {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSize;

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ValidationMessage handleValidations(MethodArgumentNotValidException exception){

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(errorValidation -> {
            errors.put(((FieldError) errorValidation).getField(), errorValidation.getDefaultMessage());
        });

        return new ValidationMessage(400, "Bad Request", errors);

    }

    @ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ValidationMessage handleMaxSizeException(MaxUploadSizeExceededException exception) {

        Map<String, String> errorMaxSize = new HashMap<>();
        errorMaxSize.put("error", "El tamaño máximo del archivo debe ser de " + maxSize);

        return new ValidationMessage(HttpStatus.EXPECTATION_FAILED.value(), "Expectation Failed", errorMaxSize);
    }
}
