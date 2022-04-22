package com.urjc.backend.modelo;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class POD {

    @EmbeddedId
    PODKey id;

    @ManyToOne
    @MapsId("asignaturaId")
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;

    @ManyToOne
    @MapsId("cursoId")
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne
    @MapsId("docenteId")
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @NotEmpty(message = "Se deben completar las horas escogidas")
    @Column(nullable = false)
    private Integer horasEscogidas;

}
