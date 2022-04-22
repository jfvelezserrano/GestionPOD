package com.urjc.backend.modelo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class CursoDocenteKey implements Serializable {

    @Column(name = "curso_id")
    Integer cursoId;

    @Column(name = "docente_id")
    Integer docenteId;

    public CursoDocenteKey() {}

    public CursoDocenteKey(Integer cursoId, Integer docenteId) {
        this.cursoId = cursoId;
        this.docenteId = docenteId;
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
        return Objects.equals(getCursoId(), that.getCursoId()) &&
                Objects.equals(getDocenteId(), that.getDocenteId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCursoId(), getDocenteId());
    }
}

