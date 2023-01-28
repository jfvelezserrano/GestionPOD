package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EmailRequestDTO {
    @Email(message = "Introduzca un email válido")
    @NotBlank(message = "Se debe completar el email")
    private String email;
}
