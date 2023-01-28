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
class CourseSubjectKey implements Serializable {

    @Column(name = "subject_id")
    Long subjectId;

    @Column(name = "course_id")
    Long courseId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseSubjectKey)) return false;
        CourseSubjectKey that = (CourseSubjectKey) o;
        return Objects.equals(getSubjectId(), that.getSubjectId()) &&
                Objects.equals(getCourseId(), that.getCourseId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubjectId(), getCourseId());
    }
}