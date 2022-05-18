package com.urjc.backend.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class PODKey implements Serializable {

    @Column(name = "subject_id")
    Long subjectId;

    @Column(name = "course_id")
    Long courseId;

    @Column(name = "teacher_id")
    Long teacherId;

    public PODKey() {}

    public PODKey(Long subjectId, Long courseId, Long teacherId) {
        this.subjectId = subjectId;
        this.courseId = courseId;
        this.teacherId = teacherId;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

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
