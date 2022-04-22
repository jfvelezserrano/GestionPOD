package com.urjc.backend.modelo;

import javax.persistence.*;

@Entity
@Table(name = "cursoAsignatura")
public class CursoAsignatura {
    @EmbeddedId
    CursoAsignaturaKey id;

    @ManyToOne
    @MapsId("cursoId")
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne
    @MapsId("asignaturaId")
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;
}
