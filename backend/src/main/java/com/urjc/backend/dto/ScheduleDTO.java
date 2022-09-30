package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDTO {
    private Long id;
    private Character dayWeek;
    private String startTime;
    private String endTime;
}
