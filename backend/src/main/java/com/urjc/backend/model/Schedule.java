package com.urjc.backend.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Entity
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private Character dayWeek;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    public Schedule() {}

    public Schedule(Character dayWeek, String startTime, String endTime) {
        this.dayWeek = dayWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}