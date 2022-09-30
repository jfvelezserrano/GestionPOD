package com.urjc.backend.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubjectDTO {

    public interface NameAndQuarter {
    }

    private Long id;
    private String code;

    @JsonView(NameAndQuarter.class)
    private String name;

    private String title;
    private Integer totalHours;
    private String campus;
    private Integer year;

    @JsonView(NameAndQuarter.class)
    private String quarter;

    private String type;
    private String turn;
    private String career;
    private List<String> assistanceCareers;
    private List<ScheduleDTO> schedules;

}
