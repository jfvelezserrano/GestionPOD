package com.urjc.backend.modelo;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "asignatura")
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotEmpty(message = "Se debe completar el codigo")
    @Column(nullable = false)
    private Integer codigo;

    @NotEmpty(message = "Se debe completar el nombre")
    @Column(nullable = false)
    private String nombre;

    @NotEmpty(message = "Se debe completar la titulacion")
    @Column(nullable = false)
    private String titulacion;

    @NotEmpty(message = "Se deben completar las horas totales")
    @Column(nullable = false)
    private Integer horasTotales;

    @NotEmpty(message = "Se debe completar el campus")
    @Column(nullable = false)
    private String campus;

    @NotEmpty(message = "Se debe completar el año")
    @Column(nullable = false)
    private Integer anio;

    @NotEmpty(message = "Se debe completar el cuatrimestre")
    @Column(nullable = false)
    private String cuatrimestre;

    @NotEmpty(message = "Se debe completar el tipo")
    @Column(nullable = false)
    private String tipo;

    @NotEmpty(message = "Se debe completar el turno")
    @Column(nullable = false)
    private String turno;

    @NotEmpty(message = "Se debe completar el grupo")
    @Column(nullable = false)
    private String grupo;

    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(unique = true, nullable = false)
    private Set<POD> pods;

    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(unique = true, nullable = false)
    private Set<CursoAsignatura> cursoAsignaturas;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> gruposAsistencia;

    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Horario> horarios;
}

