package com.urjc.backend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
public class CourseDTO {

    @Id
    private Long id;

    private String name;
}
