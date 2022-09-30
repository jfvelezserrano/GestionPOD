package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsGlobalDTO {
    private Integer percentageCharge;
    private Integer totalChosenHours;
    private Integer totalCharge;
    private Integer percentageForce;
    private Integer totalCorrectHours;
    private Integer percentageCompletations;
    private Integer numCompletations;
    private Integer numSubjects;
    private Integer numConflicts;
}
