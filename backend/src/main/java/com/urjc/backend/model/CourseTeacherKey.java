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
class CourseTeacherKey implements Serializable {

    @Column(name = "course_id")
    Long courseId;

    @Column(name = "teacher_id")
    Long teacherId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseTeacherKey)) return false;
        CourseTeacherKey that = (CourseTeacherKey) o;
        return Objects.equals(getCourseId(), that.getCourseId()) &&
                Objects.equals(getTeacherId(), that.getTeacherId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourseId(), getTeacherId());
    }
}

