package com.urjc.backend.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "subject")
public class Subject {

    public interface Base {
    }

    public interface Name {
    }

    public interface Details {
    }

    @JsonView({Base.class, Name.class})
    @Id
    @Basic(optional = false)
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView({Base.class, Name.class})
    @Column(nullable = false)
    private String code;

    @JsonView({Base.class, Name.class})
    @Column(nullable = false)
    private String name;

    @JsonView(Base.class)
    @Column(nullable = false)
    private String title;

    @JsonView(Base.class)
    @Column(nullable = false)
    private Integer totalHours;

    @JsonView(Base.class)
    @Column(nullable = false)
    private String campus;

    @JsonView(Base.class)
    @Column(nullable = false)
    private Integer year;

    @JsonView(Base.class)
    @Column(nullable = false)
    private String quarter;

    @JsonView(Base.class)
    @Column(nullable = false)
    private String type;

    @JsonView(Base.class)
    @Column(nullable = false)
    private String turn;

    @JsonView(Base.class)
    @Column(nullable = false)
    private String career;

    @JsonView(Details.class)
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<POD> pods;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(unique = true, nullable = false)
    private Set<CourseSubject> courseSubjects;

    @JsonView(Base.class)
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> assitanceCareers;

    @JsonView(Base.class)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public Set<POD> getPods() {
        return pods;
    }

    public void setPods(Set<POD> pods) {
        this.pods = pods;
    }

    public Set<CourseSubject> getCourseSubjects() { return courseSubjects; }

    public void setCourseSubjects(Set<CourseSubject> courseSubjects) { this.courseSubjects = courseSubjects; }

    public List<String> getAssitanceCareers() {
        return assitanceCareers;
    }

    public void setAssitanceCareers(List<String> assitanceCareers) {
        this.assitanceCareers = assitanceCareers;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
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
            this.assitanceCareers = values;
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
}
