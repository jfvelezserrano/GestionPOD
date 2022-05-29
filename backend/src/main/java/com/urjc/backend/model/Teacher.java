package com.urjc.backend.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "teacher")
public class Teacher{

    public interface Base {
    }

    public interface Roles {
    }

    @JsonView(Base.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(mappedBy = "teacher", orphanRemoval = true, fetch = FetchType.EAGER)
    @Column(unique = true, nullable = false)
    private Set<POD> pods;

    @OneToMany(mappedBy = "teacher")
    @Column(unique = true, nullable = false)
    private Set<CourseTeacher> courseTeachers;

    @JsonView(Roles.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @JsonView(Base.class)
    @Column(nullable = false)
    private String name;

    @JsonView(Base.class)
    @Email
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
