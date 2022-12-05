package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class EmailRequestDTO {
    @Email
    @NotBlank(message = "Se debe completar el email")
    private String email;
}
