package com.urjc.backend.mapper;

import com.urjc.backend.dto.CourseDTO;
import com.urjc.backend.model.Course;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICourseMapper {

    CourseDTO toCourseDTO(Course course);

    Course toCourse(CourseDTO course);

    List<CourseDTO> map(List<Course> courses);
}
