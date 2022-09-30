package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsGraphHoursDTO {
    private String subjectName;
    private Integer subjectTotalHours;
    private Integer teacherChosenHours;
}
