package com.urjc.backend.mapper;

import com.urjc.backend.dto.CourseDTO;
import com.urjc.backend.dto.CourseNameDTO;
import com.urjc.backend.dto.StatisticsPersonalDTO;
import com.urjc.backend.model.Course;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICourseMapper {

    CourseDTO toCourseDTO(Course course);

    List<CourseDTO> map(List<Course> courses);

    default String toNameCourse(CourseNameDTO courseNameDTO){
        return courseNameDTO.getName();
    }
}
