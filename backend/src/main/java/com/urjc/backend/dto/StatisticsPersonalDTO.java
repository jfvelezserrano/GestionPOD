package com.urjc.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsPersonalDTO {
    private Integer percentage;
    private Integer charge;
    private Integer correctedHours;
    private Integer numSubjects;
    private Integer numConflicts;
}
