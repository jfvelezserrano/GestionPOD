package com.urjc.backend.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotEmpty(message = "Se debe completar el nombre")
    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    Date creationDate;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(unique = true, nullable = false)
    private Set<POD> pods;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(unique = true, nullable = false)
    private Set<CourseTeacher> courseTeachers;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(unique = true, nullable = false)
    private Set<CourseSubject> courseSubjects;
}