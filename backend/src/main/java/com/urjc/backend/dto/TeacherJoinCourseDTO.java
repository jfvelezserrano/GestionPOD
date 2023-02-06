package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class TeacherJoinCourseDTO {

    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @NotBlank(message = "Se debe completar el nombre")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    private String name;

    @Email(message = "Introduzca un email válido")
    @NotBlank(message = "Se debe completar el email")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    private String email;

    @NotNull(message = "Se deben completar la fuerza del docente")
    @Min(value = 1, message = "El número mínimo es de {value}h")
    @Max(value = 400, message = "El número máximo es de {value}h")
    private Integer hours;
}
