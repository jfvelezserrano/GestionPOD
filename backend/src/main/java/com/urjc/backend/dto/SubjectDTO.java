package com.urjc.backend.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.validation.AssistanceCareersConstraint;
import com.urjc.backend.validation.QuarterConstraint;
import com.urjc.backend.validation.TurnConstraint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class SubjectDTO {

    public interface NameAndQuarter {
    }

    @Id
    private Long id;

    @NotBlank(message = "Se debe completar el código")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    private String code;

    @JsonView(NameAndQuarter.class)
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @NotBlank(message = "Se debe completar el nombre")
    private String name;

    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @NotBlank(message = "Se debe completar la titulación")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    private String title;

    @NotNull(message = "Se deben completar las horas totales")
    @Min(value = 0, message = "El número mínimo de horas es de {value}")
    @Max(value = 400, message = "El número máximo es de {value}h")
    private Integer totalHours;

    @NotBlank(message = "Se debe completar el campus")
    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    private String campus;

    @NotNull(message = "Se debe completar el año de impartición")
    @Min(value = 1, message = "El valor mínimo es {value}")
    @Max(value = 10, message = "El valor máximo es de {value}")
    private Integer year;

    @QuarterConstraint
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @JsonView(NameAndQuarter.class)
    private String quarter;

    @NotBlank(message = "Se debe completar el tipo de asignatura")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    private String type;

    @NotNull(message = "Se debe completar el turno")
    @TurnConstraint
    private Character turn;

    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @NotBlank(message = "Se debe completar el grupo de carrera")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    private String career;

    @AssistanceCareersConstraint
    private List<String> assistanceCareers;

    private List<@Valid ScheduleDTO> schedules;

}
