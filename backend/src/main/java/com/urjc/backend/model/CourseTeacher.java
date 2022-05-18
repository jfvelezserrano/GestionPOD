package com.urjc.backend.model;

import javax.persistence.*;

@Entity
@Table(name = "courseTeacher")
public class CourseTeacher {
    @EmbeddedId
    CourseTeacherKey id;

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
}