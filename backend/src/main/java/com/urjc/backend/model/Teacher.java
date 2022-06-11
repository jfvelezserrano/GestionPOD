package com.urjc.backend.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.*;

@Entity
@Table(name = "teacher")
public class Teacher{

    public interface Base {
    }

    public interface Roles {
    }

    public interface Name {
    }

    @JsonView(Base.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(mappedBy = "teacher", orphanRemoval = true, cascade = CascadeType.ALL)
    @Column(unique = true, nullable = false)
    private Set<POD> pods;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(unique = true, nullable = false)
    private Set<CourseTeacher> courseTeachers;

    @JsonView(Roles.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @JsonView({Base.class, Name.class})
    @Column(nullable = false)
    private String name;

    @JsonView(Base.class)
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    public Teacher(String name, String email) {
        this.roles = new ArrayList<>();
        roles.add("TEACHER");
        this.name = name;
        this.email = email;
        this.courseTeachers = new HashSet<>();
        this.pods = new HashSet<>();
    }

    public Teacher(List<String> roles, String name, String email) {
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.courseTeachers = new HashSet<>();
        this.pods = new HashSet<>();
    }

    public Teacher() {
        this.courseTeachers = new HashSet<>();
        this.pods = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void addChosenSubject(Subject subject, Course course, Integer hours) {
        POD pod = new POD();
        pod.setTeacher(this);
        pod.setSubject(subject);
        pod.setCourse(course);
        pod.setChosenHours(hours);

        this.pods.add(pod);
    }

    public void deleteChosenSubject(POD pod) {
        pods.remove(pod);
    }

    public POD hasSubjectInCourse(Subject subject, Course course){
        for (POD pod : pods) {
            if (pod.getTeacher().equals(this) && pod.getSubject().equals(subject) && (pod.getCourse().equals(course))) {
                return pod;
            }
        }
        return null;
    }
}
