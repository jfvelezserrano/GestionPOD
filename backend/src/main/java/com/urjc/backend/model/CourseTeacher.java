package com.urjc.backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "courseTeacher")
public class CourseTeacher {

    @EmbeddedId
    private CourseTeacherKey id = new CourseTeacherKey();

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
    @NotNull(message = "Se debe completar la fuerza del docente")
    @Min(value = 1, message = "El valor mínimo es {value}")
    @Max(value = 400, message = "El valor máximo es {value}")
    private Integer originalHours;

    @Column(length = 1000)
    private String observation;
}