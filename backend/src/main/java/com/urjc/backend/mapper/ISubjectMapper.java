package com.urjc.backend.mapper;

import com.urjc.backend.dto.SubjectDTO;
import com.urjc.backend.dto.SubjectTeacherDTO;
import com.urjc.backend.model.Subject;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ISubjectMapper {

    SubjectDTO toSubjectDTO(Subject subject);

    Subject toSubject(SubjectDTO subjectDTO);

    List<SubjectDTO> listSubjectDTO(List<Subject> subjects);

    @Mapping(target = "subject", expression = "java(toSubjectDTO(subject))")
    SubjectTeacherDTO toSubjectTeacherDTO(Subject subject, List<String> joinedTeachers, Integer leftHours, List<String> conflicts);

    default List<SubjectTeacherDTO> listSubjectTeacherDTOs(List<Object[]> listItems){
        if ( listItems == null ) {
            return Collections.emptyList();
        }

        List<SubjectTeacherDTO> subjectTeacherDTOList = new ArrayList<>();

        for (Object[] item: listItems) {
            SubjectTeacherDTO subjectTeacherDTO = new SubjectTeacherDTO();

            if(item.length == 2){
                subjectTeacherDTO = toSubjectTeacherDTO(((Subject) item[0]), ((List<String>) item[1]), 0, null);
            } else if (item.length == 3){
                subjectTeacherDTO = toSubjectTeacherDTO(((Subject) item[0]), ((List<String>) item[1]), ((Integer) item[2]), null);
            } else if (item.length == 4){
                subjectTeacherDTO = toSubjectTeacherDTO(((Subject) item[0]), ((List<String>) item[1]), ((Integer) item[2]), ((List<String>) item[3]));
            }
            subjectTeacherDTOList.add(subjectTeacherDTO);
        }

        return subjectTeacherDTOList;
    }
}
