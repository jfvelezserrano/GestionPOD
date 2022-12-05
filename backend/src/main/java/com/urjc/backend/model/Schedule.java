package com.urjc.backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.regex.Pattern;

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

    public Boolean isValid(){
        String pattern = "[0-9]{2}:[0-9]{2}";
        return (this.dayWeek.equals('L') || this.dayWeek.equals('M') || this.dayWeek.equals('X') || this.dayWeek.equals('J') || this.dayWeek.equals('V'))
                && Pattern.matches(pattern, this.startTime) && Pattern.matches(pattern, this.endTime);
    }
}