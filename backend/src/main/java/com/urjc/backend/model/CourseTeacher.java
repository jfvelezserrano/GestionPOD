package com.urjc.backend.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "courseTeacher")
public class CourseTeacher {

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

    @Column(nullable = false)
    private Integer correctedHours;

    @Column(nullable = false)
    private Integer originalHours;

    private String observation;

    public CourseTeacher() {}

    public CourseTeacher(Course course, Teacher teacher, Integer correctedHours, Integer originalHours, String observation) {
        this.course = course;
        this.teacher = teacher;
        this.correctedHours = correctedHours;
        this.originalHours = originalHours;
        this.observation = observation;
    }
}