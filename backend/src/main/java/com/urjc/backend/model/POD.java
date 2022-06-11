package com.urjc.backend.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class POD {

    public interface Base {
    }

    @EmbeddedId
    PODKey id = new PODKey();

    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @JsonView(Base.class)
    @ManyToOne
    @MapsId("teacherId")
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @JsonView(Base.class)
    @Column(nullable = false)
    private Integer chosenHours;


    public POD() {}

    public PODKey getId() {
        return id;
    }

    public void setId(PODKey id) {
        this.id = id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Integer getChosenHours() {
        return chosenHours;
    }

    public void setChosenHours(Integer chosenHours) {
        this.chosenHours = chosenHours;
    }
}
