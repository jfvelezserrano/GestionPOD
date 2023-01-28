package com.urjc.backend.model;

import com.urjc.backend.error.exception.CSVValidationException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "teacher")
public class Teacher{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(unique = true, nullable = false)
    private Set<POD> pods;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(unique = true, nullable = false)
    private Set<CourseTeacher> courseTeachers;

    @NotNull(message = "Se deben añadir roles")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @NotBlank(message = "Se debe completar el nombre")
    @Column(nullable = false)
    private String name;

    @Email(message = "Introduzca un email válido")
    @NotBlank(message = "Se debe completar el email")
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

    public void validate(String line){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> errors = validator.validate(this);

        if(!validator.validate(this).isEmpty()){
            throw new CSVValidationException("Hay datos incorrectos y/o incompletos en el siguiente docente: " + line, errors);
        }
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

    public void editEditableData(Course course, Integer correctedHours, String observation) {
        CourseTeacher courseTeacherToEdit = new CourseTeacher();
        for (CourseTeacher courseTeacher : courseTeachers) {
            if (courseTeacher.getTeacher().equals(this) && courseTeacher.getCourse().equals(course)) {
                courseTeacherToEdit = courseTeacher;
                break;
            }
        }
        courseTeacherToEdit.setCorrectedHours(correctedHours);
        String observationToSave = (observation == null || observation.isEmpty()) ? null : observation;
        courseTeacherToEdit.setObservation(observationToSave);
    }

    public void unjoinCourse(Course course){
        CourseTeacher courseTeacherToUnjoin = new CourseTeacher();
        for (CourseTeacher courseTeacher : courseTeachers) {
            if (courseTeacher.getCourse().equals(course) && courseTeacher.getTeacher().equals(this)) {
                courseTeacherToUnjoin = courseTeacher;
                break;
            }
        }
        courseTeachers.remove(courseTeacherToUnjoin);
    }
}
