package com.urjc.backend.model;

import com.urjc.backend.validation.DayWeekConstraint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Entity
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Se debe completar el día de la semana")
    @DayWeekConstraint
    @Column(nullable = false)
    private Character dayWeek;

    @Pattern(regexp = "\\d{2}:\\d{2}", message = "La hora de inicio se debe escribir con el siguiente formato numérico: 00:00")
    @NotBlank(message = "Se debe completar la hora de inicio")
    @Column(nullable = false)
    private String startTime;

    @Pattern(regexp = "\\d{2}:\\d{2}", message = "La hora de fin se debe escribir con el siguiente formato numérico: 00:00")
    @NotBlank(message = "Se debe completar la hora de fin")
    @Column(nullable = false)
    private String endTime;

    public Schedule() {}

    public Schedule(Character dayWeek, String startTime, String endTime) {
        this.dayWeek = dayWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}