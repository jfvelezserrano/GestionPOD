package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsTeacherDTO {
    private String name;
    private Integer originalHours;
    private Integer correctedHours;
    private String observation;
    private Integer percentage;
    private Integer charge;
    private Integer numSubjects;
}
