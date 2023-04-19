package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class CourseTeacherDTO {

    @NotNull(message = "Se debe indicar la fuerza del docente")
    @Min(value = 1, message = "El número mínimo es de {value}h")
    @Max(value = 400, message = "El número máximo es de {value}h")
    private Integer correctedHours;

    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @Size(max = 1000, message = "El texto permite un máximo de {max} caracteres")
    private String observation;
}
