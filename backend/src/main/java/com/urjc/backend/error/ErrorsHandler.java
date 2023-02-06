package com.urjc.backend.error;

import com.urjc.backend.error.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorsHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSize;


    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleGlobalRequest(GlobalException e){
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus(), e.getMessage());
        return new ResponseEntity<>(errorResponse, e.getStatus());
    }

    @ExceptionHandler(RedirectException.class)
    public ResponseEntity<ErrorResponse> handleRedirect(RedirectException e){
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus(), e.getMessage(), true);
        return new ResponseEntity<>(errorResponse, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e){

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(errorValidation ->
                errors.put(((FieldError) errorValidation).getField(), errorValidation.getDefaultMessage()));

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Entrada no válida", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CSVValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidations(CSVValidationException e){
        Map<String, String> errors = new HashMap<>();

        e.getViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.EXPECTATION_FAILED, "El tamaño máximo del archivo debe ser de " + maxSize);
        return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleBadRequest() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,"Se ha producido un error con los datos", true);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, "Operación denegada", true);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({InvalidDataAccessApiUsageException.class, DataAccessException.class})
    protected ResponseEntity<Object> handleConflict() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, "Operación denegada por uso incorrecto", true);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<ErrorResponse> handleExceptions(){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Se ha producido un error", true);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}