package com.urjc.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
