package com.urjc.backend.mapper;

import com.urjc.backend.dto.CourseTeacherDTO;
import com.urjc.backend.dto.TeacherDTO;
import com.urjc.backend.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ITeacherMapper {

    ITeacherMapper INSTANCE = Mappers.getMapper(ITeacherMapper.class);

    TeacherDTO toTeacherDTO(Teacher teacher);

    List<TeacherDTO> map(List<Teacher> teachers);

    CourseTeacherDTO toTeacherEditableDataDTO(Integer correctedHours, String observation);

}