package com.urjc.backend.model;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class CourseSubjectKey implements Serializable {

    @Column(name = "subject_id")
    Long subjectId;

    @Column(name = "course_id")
    Long courseId;


    public CourseSubjectKey() {}

    public CourseSubjectKey(Long subjectId, Long courseId) {
        this.subjectId = subjectId;
        this.courseId = courseId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

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