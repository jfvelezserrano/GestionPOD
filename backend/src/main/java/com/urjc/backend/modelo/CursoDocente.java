package com.urjc.backend.modelo;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "cursoDocente")
public class CursoDocente {
    @EmbeddedId
    CursoDocenteKey id;

    @ManyToOne
    @MapsId("cursoId")
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne
    @MapsId("docenteId")
    @JoinColumn(name = "docente_id")
    private Docente docente;

    private Integer horasCorregidas;

    @Column(nullable = false)
    private Integer horasOriginales;

    private String observacion;
}