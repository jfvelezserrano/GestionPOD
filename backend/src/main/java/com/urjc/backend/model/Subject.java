package com.urjc.backend.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Se debe completar el codigo")
    @Column(nullable = false)
    private Long code;

    @NotEmpty(message = "Se debe completar el nombre")
    @Column(nullable = false)
    private String name;

    @NotEmpty(message = "Se debe completar la titulacion")
    @Column(nullable = false)
    private String title;

    @NotEmpty(message = "Se deben completar las horas totales")
    @Column(nullable = false)
    private Integer totalHours;

    @NotEmpty(message = "Se debe completar el campus")
    @Column(nullable = false)
    private String campus;

    @NotEmpty(message = "Se debe completar el año")
    @Column(nullable = false)
    private Integer year;

    @NotEmpty(message = "Se debe completar el cuatrimestre")
    @Column(nullable = false)
    private String quarter;

    @NotEmpty(message = "Se debe completar el tipo")
    @Column(nullable = false)
    private String type;

    @NotEmpty(message = "Se debe completar el turno")
    @Column(nullable = false)
    private String turn;

    @NotEmpty(message = "Se debe completar el grupo")
    @Column(nullable = false)
    private String career;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(unique = true, nullable = false)
    private Set<POD> pods;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(unique = true, nullable = false)
    private Set<CourseSubject> courseSubjects;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> assitanceCareers;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Schedule> schedules;
}
