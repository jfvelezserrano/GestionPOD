package com.urjc.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.controller.CourseRestController;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubjectTeacherDTO {

    public interface Base extends SubjectDTO.NameAndQuarter{
    }

    public interface Status {
    }

    public interface Conflicts {
    }

    @JsonView(Base.class)
    private SubjectDTO subject;

    @JsonView(Base.class)
    private List<String> joinedTeachers;

    @JsonView(Status.class)
    private Integer leftHours;

    @JsonView(Conflicts.class)
    private List<String> conflicts;

}
