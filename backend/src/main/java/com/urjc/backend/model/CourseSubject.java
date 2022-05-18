package com.urjc.backend.model;

import javax.persistence.*;

@Entity
@Table(name = "courseSubject")
public class CourseSubject {
    @EmbeddedId
    CourseSubjectKey id;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    private Subject subject;
}
