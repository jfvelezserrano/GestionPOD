package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsPersonalDTO {
    private Integer percentage;
    private Integer charge;
    private Integer correctedHours;
    private Integer numSubjects;
    private Integer numConflicts;
}
