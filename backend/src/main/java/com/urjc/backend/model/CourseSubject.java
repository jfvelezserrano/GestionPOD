package com.urjc.backend.model;

import javax.persistence.*;

@Entity
@Table(name = "courseSubject")
public class CourseSubject {
    @EmbeddedId
    CourseSubjectKey id = new CourseSubjectKey();

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public CourseSubjectKey getId() {
        return id;
    }

    public void setId(CourseSubjectKey id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}