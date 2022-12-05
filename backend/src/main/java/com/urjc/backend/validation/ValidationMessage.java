package com.urjc.backend.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ValidationMessage {
    private int status;
    private String error;
    private Map<String,String> errors;
}
