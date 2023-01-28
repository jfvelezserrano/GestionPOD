package com.urjc.backend.dto;

import com.urjc.backend.validation.DayWeekConstraint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.validation.constraints.*;

@Getter
@Setter
public class ScheduleDTO {
    @Id
    private Long id;

    @NotNull(message = "Se debe completar el día de la semana")
    @DayWeekConstraint
    private Character dayWeek;

    @Pattern(regexp = "\\d{2}:\\d{2}", message = "La hora de inicio se debe escribir con el siguiente formato numérico: 00:00")
    @NotBlank(message = "Se debe completar la hora de inicio")
    private String startTime;

    @Pattern(regexp = "\\d{2}:\\d{2}", message = "La hora de fin se debe escribir con el siguiente formato numérico: 00:00")
    @NotBlank(message = "Se debe completar la hora de fin")
    private String endTime;
}
