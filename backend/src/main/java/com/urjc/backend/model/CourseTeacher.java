package com.urjc.backend.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
@Table(name = "courseTeacher")
public class CourseTeacher {

    public interface DataToEdit {
    }

    @EmbeddedId
    CourseTeacherKey id = new CourseTeacherKey();

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @MapsId("teacherId")
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @JsonView(DataToEdit.class)
    @Column(nullable = false)
    private Integer correctedHours;

    @Column(nullable = false)
    private Integer originalHours;

    @JsonView(DataToEdit.class)
    private String observation;

    public CourseTeacher() {}

    public CourseTeacher(Course course, Teacher teacher, Integer correctedHours, Integer originalHours, String observation) {
        this.course = course;
        this.teacher = teacher;
        this.correctedHours = correctedHours;
        this.originalHours = originalHours;
        this.observation = observation;
    }

    public CourseTeacherKey getId() {
        return id;
    }

    public void setId(CourseTeacherKey id) {
        this.id = id;
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

    public Integer getCorrectedHours() {
        return correctedHours;
    }

    public void setCorrectedHours(Integer correctedHours) {
        this.correctedHours = correctedHours;
    }

    public Integer getOriginalHours() {
        return originalHours;
    }

    public void setOriginalHours(Integer originalHours) {
        this.originalHours = originalHours;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}