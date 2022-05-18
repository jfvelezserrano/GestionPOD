package com.urjc.backend.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class CourseTeacherKey implements Serializable {

    @Column(name = "course_id")
    Long courseId;

    @Column(name = "teacher_id")
    Long teacherId;

    public CourseTeacherKey() {}

    public CourseTeacherKey(Long courseId, Long teacherId) {
        this.courseId = courseId;
        this.teacherId = teacherId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

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

