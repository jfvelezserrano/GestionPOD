package com.urjc.backend.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teacher")
public class Teacher{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Column(unique = true, nullable = false)
    private Set<POD> pods;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Column(unique = true, nullable = false)
    private Set<CourseTeacher> courseTeachers;

    @NotEmpty(message = "Se debe completar el rol")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @NotEmpty(message = "Se debe completar el nombre")
    @Column(nullable = false)
    private String name;

    @Email
    @NotEmpty(message = "Se debe completar el email")
    @Column(nullable = false, unique = true)
    private String email;

    public Teacher(List<String> roles, String name, String email) {
        this.roles = roles;
        this.name = name;
        this.email = email;
    }

    public Teacher() {}

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Set<POD> getPods() {
        return pods;
    }

    public void setPods(Set<POD> pods) {
        this.pods = pods;
    }

    public Set<CourseTeacher> getCourseTeachers() {
        return courseTeachers;
    }

    public void setCourseTeachers(Set<CourseTeacher> courseTeachers) {
        this.courseTeachers = courseTeachers;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
