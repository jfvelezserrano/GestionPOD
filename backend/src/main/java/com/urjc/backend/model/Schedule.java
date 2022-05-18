package com.urjc.backend.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @NotEmpty(message = "Se debe completar el dia de la semana")
    @Column(nullable = false)
    private String dayWeek;

    @NotEmpty(message = "Se debe completar la hora de inicio")
    @Column(nullable = false)
    private String startTime;

    @NotEmpty(message = "Se debe completar la hora de fin")
    @Column(nullable = false)
    private String endTime;
}