package com.urjc.backend.mapper;

import com.urjc.backend.dto.CourseTeacherDTO;
import com.urjc.backend.dto.TeacherDTO;
import com.urjc.backend.model.Teacher;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ITeacherMapper {

    TeacherDTO toTeacherDTO(Teacher teacher);

    Teacher toTeacher(TeacherDTO teacherDTO);

    List<TeacherDTO> map(List<Teacher> teachers);

    CourseTeacherDTO toTeacherEditableDataDTO(Integer correctedHours, String observation);

}
