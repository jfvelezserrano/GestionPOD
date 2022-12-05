package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CourseNameDTO {
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @NotBlank(message = "Se debe completar el nombre del curso")
    private String name;
}
