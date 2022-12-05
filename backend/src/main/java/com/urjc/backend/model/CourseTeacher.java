package com.urjc.backend.model;

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

    @Column(length = 1000)
    private String observation;

    public CourseTeacher() {}
}