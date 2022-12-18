package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TeacherJoinSubjectDTO {

    @NotNull(message = "Se deben completar las horas")
    @Min(value = 0, message = "El número mínimo de horas es de {value}")
    @Max(value = 400, message = "El número máximo de horas es de {value}")
    private Integer hours;
}
