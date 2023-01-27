package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class TeacherJoinSubjectDTO {

    @NotNull(message = "Se deben completar las horas elegidas por el docente")
    @Min(value = 0, message = "El número mínimo de horas es de {value}")
    @Max(value = 400, message = "El número máximo de horas es de {value}")
    private Integer hours;
}
