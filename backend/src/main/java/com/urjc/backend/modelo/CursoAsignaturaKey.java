package com.urjc.backend.modelo;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class CursoAsignaturaKey implements Serializable {

    @Column(name = "asignatura_id")
    Integer asignaturaId;

    @Column(name = "curso_id")
    Integer cursoId;


    public CursoAsignaturaKey() {}

    public CursoAsignaturaKey(Integer asignaturaId, Integer cursoId) {
        this.asignaturaId = asignaturaId;
        this.cursoId = cursoId;
    }

    public Integer getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Integer asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public Integer getCursoId() {
        return cursoId;
    }

    public void setCursoId(Integer cursoId) {
        this.cursoId = cursoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PODKey)) return false;
        PODKey that = (PODKey) o;
        return Objects.equals(getAsignaturaId(), that.getAsignaturaId()) &&
                Objects.equals(getCursoId(), that.getCursoId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAsignaturaId(), getCursoId());
    }
}
