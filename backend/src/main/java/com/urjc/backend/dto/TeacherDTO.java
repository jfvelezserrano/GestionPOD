package com.urjc.backend.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeacherDTO {

    public interface Base {
    }

    @JsonView(Base.class)
    private Long id;

    private List<String> roles;

    @JsonView(Base.class)
    private String name;

    @JsonView(Base.class)
    private String email;
}
