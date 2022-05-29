package com.urjc.backend.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "schedule")
public class Schedule {

    public interface Base {
    }

    @JsonView(Base.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonView(Base.class)
    @Column(nullable = false)
    private Character dayWeek;

    @JsonView(Base.class)
    @Column(nullable = false)
    private String startTime;

    @JsonView(Base.class)
    @Column(nullable = false)
    private String endTime;

    public Schedule() {}

    public Schedule(Character dayWeek, String startTime, String endTime) {
        this.dayWeek = dayWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Character getDayWeek() {
        return dayWeek;
    }

    public void setDayWeek(Character dayWeek) {
        this.dayWeek = dayWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}