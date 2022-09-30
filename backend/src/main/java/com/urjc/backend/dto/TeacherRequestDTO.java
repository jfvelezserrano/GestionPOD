package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherRequestDTO {
    private String email;
    private String name;
    private Integer hours;
}
