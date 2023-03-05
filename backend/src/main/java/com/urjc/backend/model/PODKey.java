package com.urjc.backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
class PODKey implements Serializable {

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PODKey)) return false;
        PODKey that = (PODKey) o;
        return Objects.equals(getSubjectId(), that.getSubjectId()) &&
                Objects.equals(getCourseId(), that.getCourseId()) &&
                Objects.equals(getTeacherId(), that.getTeacherId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubjectId(), getCourseId(), getTeacherId());
    }
}
