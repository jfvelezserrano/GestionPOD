package com.urjc.backend.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class POD {

    @EmbeddedId
    PODKey id;

    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @MapsId("teacherId")
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @NotEmpty(message = "Se deben completar las horas escogidas")
    @Column(nullable = false)
    private Integer chosenHours;

}
