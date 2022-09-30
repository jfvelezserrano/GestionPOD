package com.urjc.backend.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @Basic(optional = false)
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer totalHours;

    @Column(nullable = false)
    private String campus;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String quarter;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String turn;

    @Column(nullable = false)
    private String career;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<POD> pods;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(unique = true, nullable = false)
    private Set<CourseSubject> courseSubjects;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> assistanceCareers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "subject_id")
    private List<Schedule> schedules;

    public Subject(){
        this.pods = new HashSet<>();
        this.courseSubjects = new HashSet<>();
    }

    public Subject(String title, String quarter, String turn){
        this.id = 1L;
        this.title = title;
        this.quarter = quarter;
        this.turn = turn;
        this.pods = new HashSet<>();
        this.courseSubjects = new HashSet<>();
    }

    public Subject(String code, String name, String title, Integer totalHours, String campus, Integer year,
                   String quarter, String type, String turn, String career) {
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
    }

    public void setSchedulesByString(String schedules) {
        if(!schedules.equals("")) {
            String[] values = schedules.split(", ");
            List<Schedule> schedulesSet = new ArrayList<>();

            for (String value : values) {
                value = value.replaceAll("[()-]", "");
                value = value.replace(" ", "");

                Schedule schedule = new Schedule(value.charAt(0), value.substring(1, 6), value.substring(6));
                schedulesSet.add(schedule);
            }
            setSchedules(schedulesSet);
        }
    }

    public void setAssistanceCareersByString(String assistanceCareer) {
        if(!assistanceCareer.equals("")) {
            List<String> values = List.of(assistanceCareer.split(", "));
            this.assistanceCareers = values;
        }
    }

    public Map<String, List<String>> recordSubject(){
        Map<String, List<String>> recordMap = new HashMap<>();

        for (POD pod:this.getPods()) {
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
