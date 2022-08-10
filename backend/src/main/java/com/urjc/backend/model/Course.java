package com.urjc.backend.model;

import javax.persistence.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.service.TeacherService;

@Entity
@Table(name = "course")
public class Course {

    public interface Base {
    }

    public interface Name {
    }


    @JsonView(Base.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonView({Base.class, Name.class})
    @Column(nullable = false, unique = true)
    String name;

    @Column(nullable = false)
    Date creationDate;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(unique = true, nullable = false)
    private Set<POD> pods;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL,  orphanRemoval = true)
    @Column(unique = true, nullable = false)
    private Set<CourseTeacher> courseTeachers = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(unique = true, nullable = false)
    private Set<CourseSubject> courseSubjects = new HashSet<>();

    public Course() {}

    public Course(String name) {
        this.name = name;
        this.creationDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<CourseTeacher> getCourseTeachers() { return courseTeachers; }

    public void setCourseTeachers(Set<CourseTeacher> courseTeachers) { this.courseTeachers = courseTeachers; }

    public Set<CourseSubject> getCourseSubjects() { return courseSubjects; }

    public void setCourseSubjects(Set<CourseSubject> courseSubjects) { this.courseSubjects = courseSubjects; }

    public void addTeacher(Teacher teacher, Integer hours) {
        CourseTeacher newCourseTeacher = new CourseTeacher();
        newCourseTeacher.setCourse(this);
        newCourseTeacher.setTeacher(teacher);
        newCourseTeacher.setCorrectedHours(hours);
        newCourseTeacher.setOriginalHours(hours);
        newCourseTeacher.setObservation("");

        this.courseTeachers.add(newCourseTeacher);
    }

    public void addSubject(Subject subject) {
        CourseSubject newCourseSubject = new CourseSubject();
        newCourseSubject.setCourse(this);
        newCourseSubject.setSubject(subject);

        this.courseSubjects.add(newCourseSubject);
    }

    public void deleteTeacher(Teacher teacher) {
        CourseTeacher courseTeacherToDelete = new CourseTeacher();
        for (CourseTeacher courseTeacher : courseTeachers) {
            if (courseTeacher.getCourse().equals(this) && courseTeacher.getTeacher().equals(teacher)) {
                courseTeacherToDelete = courseTeacher;
                break;
            }
        }
        courseTeachers.remove(courseTeacherToDelete);

        POD podToDelete = new POD();
        for (POD pod : pods) {
            if (pod.getCourse().equals(this) && pod.getTeacher().equals(teacher)) {
                podToDelete = pod;
                break;
            }
        }
        pods.remove(podToDelete);
    }

    public void deleteSubject(Subject subject) {
        CourseSubject courseSubjectToDelete = new CourseSubject();
        for (CourseSubject courseSubject : courseSubjects) {
            if (courseSubject.getCourse().equals(this) && courseSubject.getSubject().equals(subject)) {
                courseSubjectToDelete = courseSubject;
                break;
            }
        }
        courseSubjects.remove(courseSubjectToDelete);

        POD podToDelete = new POD();
        for (POD pod : pods) {
            if (pod.getCourse().equals(this) && pod.getSubject().equals(subject)) {
                podToDelete = pod;
                break;
            }
        }
        pods.remove(podToDelete);
    }

    public Boolean isTeacherInCourse(Teacher teacher){
        for (CourseTeacher courseTeacher : courseTeachers) {
            if (courseTeacher.getCourse().equals(this) && courseTeacher.getTeacher().equals(teacher)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isSubjectInCourse(Subject subject){
        for (CourseSubject courseSubject : courseSubjects) {
            if (courseSubject.getCourse().equals(this) && courseSubject.getSubject().equals(subject)) {
                return true;
            }
        }
        return false;
    }
}