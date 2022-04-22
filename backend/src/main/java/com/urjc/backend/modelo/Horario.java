package com.urjc.backend.modelo;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "horario")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "asignatura", nullable = false)
    private Asignatura asignatura;

    @NotEmpty(message = "Se debe completar el dia de la semana")
    @Column(nullable = false)
    private String diaSemana;

    @NotEmpty(message = "Se debe completar la hora de inicio")
    @Column(nullable = false)
    private String horaInicio;

    @NotEmpty(message = "Se debe completar la hora de fin")
    @Column(nullable = false)
    private String horaFin;
}
