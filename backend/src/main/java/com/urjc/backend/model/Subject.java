package com.urjc.backend.model;

import com.urjc.backend.error.exception.CSVValidationException;
import com.urjc.backend.validation.AssistanceCareersConstraint;
import com.urjc.backend.validation.QuarterConstraint;
import com.urjc.backend.validation.TurnConstraint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Se debe completar el código")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @Column(nullable = false)
    private String code;

    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @NotBlank(message = "Se debe completar el nombre")
    @Column(nullable = false)
    private String name;

    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @NotBlank(message = "Se debe completar la titulación")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Se deben completar las horas totales")
    @Min(value = 0, message = "El número mínimo de horas es de {value}")
    @Max(value = 400, message = "El número máximo es de {value}h")
    @Column(nullable = false)
    private Integer totalHours;

    @NotBlank(message = "Se debe completar el campus")
    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Column(nullable = false)
    private String campus;

    @NotNull(message = "Se debe completar el año de impartición")
    @Min(value = 1, message = "El valor mínimo es {value}")
    @Max(value = 10, message = "El valor máximo es de {value}")
    @Column(nullable = false)
    private Integer year;

    @QuarterConstraint
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Column(nullable = false)
    private String quarter;

    @NotBlank(message = "Se debe completar el tipo de asignatura")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @Column(nullable = false)
    private String type;

    @NotNull(message = "Se debe completar el turno")
    @TurnConstraint
    @Column(nullable = false)
    private Character turn;

    @Pattern(regexp = "[^\\[\\]<>'\";!=]*", message = "Los siguientes caracteres no están permitidos: []<>'\";!=")
    @NotBlank(message = "Se debe completar el grupo de carrera")
    @Size(max = 255, message = "El texto permite un máximo de {max} caracteres")
    @Column(nullable = false)
    private String career;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<POD> pods;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseSubject> courseSubjects;

    @AssistanceCareersConstraint
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> assistanceCareers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "subject_id")
    private List<@Valid Schedule> schedules;

    public Subject(){
        this.pods = new HashSet<>();
        this.courseSubjects = new HashSet<>();
    }

    public Subject(String code, String name, String title, Integer totalHours, String campus, Integer year,
                   String quarter, String type, Character turn, String career) {
        this.code = code;
        this.name = name;
        this.title = title;
        this.totalHours = totalHours;
        this.campus = campus;
        this.year = year;
        this.quarter = quarter;
        this.type = type;
        this.turn = turn;
        this.career = career;
        this.pods = new HashSet<>();
        this.courseSubjects = new HashSet<>();
        this.assistanceCareers = new ArrayList<>();
        this.schedules = new ArrayList<>();
    }

    public void setSchedulesByString(String schedules) {
        if(!schedules.isBlank()) {
            String[] values = schedules.split(",");
            List<Schedule> schedulesSet = new ArrayList<>();

            for (String value : values) {
                String result  = value.replaceAll("[()-]", "");
                result = result.replace(" ", "");

                if(result.length() < 6){result = result.concat("concat");}
                Schedule schedule = new Schedule(result.charAt(0), result.substring(1, 6), result.substring(6));
                schedulesSet.add(schedule);
            }
            setSchedules(schedulesSet);
        }
    }

    public void validate(String line){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> errors = validator.validate(this);

        if(!validator.validate(this).isEmpty()){
            throw new CSVValidationException("Hay datos incorrectos y/o incompletos en la siguiente asignatura: " + line, errors);
        }
    }

    public void setAssistanceCareersByString(String assistanceCareer) {
        if(!assistanceCareer.isBlank()) {
            this.assistanceCareers = List.of(assistanceCareer.split(","));
        }
    }

    public Map<String, List<String>> recordSubject(){
        Map<String, List<String>> recordMap = new LinkedHashMap<>();

        List<POD> podsList = new ArrayList<>(this.getPods());
        Collections.sort(podsList, Comparator.comparing(p -> p.getCourse().getCreationDate()));

        for (POD pod:podsList) {
            List<String> values = new ArrayList<>();
            String courseName = pod.getCourse().getName();
            if(recordMap.containsKey(courseName)){
                values = recordMap.get(courseName);
            }
            values.add(pod.getChosenHours() + "h " + pod.getTeacher().getName());
            recordMap.put(courseName, values);
        }

        return recordMap;
    }

    public void unjoinCourse(Course course){
        CourseSubject courseSubjectToUnjoin = new CourseSubject();
        for (CourseSubject courseSubject : courseSubjects) {
            if (courseSubject.getCourse().equals(course) && courseSubject.getSubject().equals(this)) {
                courseSubjectToUnjoin = courseSubject;
                break;
            }
        }
        courseSubjects.remove(courseSubjectToUnjoin);
    }
}
