package com.urjc.backend.modelo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class PODKey implements Serializable {

    @Column(name = "asignatura_id")
    Integer asignaturaId;

    @Column(name = "curso_id")
    Integer cursoId;

    @Column(name = "docente_id")
    Integer docenteId;

    public PODKey() {}

    public PODKey(Integer asignaturaId, Integer cursoId, Integer docenteId) {
        this.asignaturaId = asignaturaId;
        this.cursoId = cursoId;
        this.docenteId = docenteId;
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

    public Integer getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Integer docenteId) {
        this.docenteId = docenteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PODKey)) return false;
        PODKey that = (PODKey) o;
        return Objects.equals(getAsignaturaId(), that.getAsignaturaId()) &&
                Objects.equals(getCursoId(), that.getCursoId()) &&
                Objects.equals(getDocenteId(), that.getDocenteId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAsignaturaId(), getCursoId(), getDocenteId());
    }
}
