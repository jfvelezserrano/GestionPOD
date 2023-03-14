package com.urjc.backend.mapper;

import com.urjc.backend.dto.*;
import com.urjc.backend.model.CourseTeacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IStatisticsMapper {

    StatisticsMatesDTO toStatisticsMatesDTO(String mateName, String mateSubject, Integer matePercentage);

    default List<StatisticsMatesDTO> listStatisticsMatesDTO(List<Object[]> listItems){
        if ( listItems == null ) {
            return Collections.emptyList();
        }

        List<StatisticsMatesDTO> statisticsMatesDTOS = new ArrayList<>();

        for (Object[] item: listItems) {
            StatisticsMatesDTO statisticsMatesDTO = toStatisticsMatesDTO(((String) item[0]), ((String) item[1]), ((Integer) item[2]));
            statisticsMatesDTOS.add(statisticsMatesDTO);
        }

        return statisticsMatesDTOS;
    }

    StatisticsGraphHoursDTO toStatisticsGraphHoursDTO(String subjectName, Integer subjectTotalHours, Integer teacherChosenHours);

    default List<StatisticsGraphHoursDTO> listStatisticsGraphHoursDTO(List<Object[]> listItems){
        if ( listItems == null ) {
            return Collections.emptyList();
        }

        List<StatisticsGraphHoursDTO> statisticsGraphHoursDTOS = new ArrayList<>();

        for (Object[] item: listItems) {
            StatisticsGraphHoursDTO statisticsGraphHoursDTO = toStatisticsGraphHoursDTO(((String) item[0]), ((Integer) item[1]), ((Integer) item[2]));
            statisticsGraphHoursDTOS.add(statisticsGraphHoursDTO);
        }

        return statisticsGraphHoursDTOS;
    }

    StatisticsGraphPercentageDTO toStatisticsGraphPercentageDTO(String subjectName, Integer teacherHoursPercentage);

    default List<StatisticsGraphPercentageDTO> listStatisticsGraphPercentageDTO(List<Object[]> listItems){
        if ( listItems == null ) {
            return Collections.emptyList();
        }

        List<StatisticsGraphPercentageDTO> statisticsGraphPercentageDTOS = new ArrayList<>();

        for (Object[] item: listItems) {
            StatisticsGraphPercentageDTO statisticsGraphPercentageDTO = toStatisticsGraphPercentageDTO(((String) item[0]), ((Integer) item[1]));
            statisticsGraphPercentageDTOS.add(statisticsGraphPercentageDTO);
        }

        return statisticsGraphPercentageDTOS;
    }

    @Mapping( target = "originalHours", source = "courseTeacher.originalHours" )
    @Mapping( target = "correctedHours", source = "courseTeacher.correctedHours" )
    @Mapping( target = "observation", source = "courseTeacher.observation" )
    StatisticsTeacherDTO toStatisticsTeachersDTO(String name, CourseTeacher courseTeacher, Integer percentage, Integer charge, Integer numSubjects);

    default List<StatisticsTeacherDTO> listStatisticsTeachersDTO(List<Object[]> listItems){
        if ( listItems == null ) {
            return Collections.emptyList();
        }

        List<StatisticsTeacherDTO> statisticsTeacherDTOS = new ArrayList<>();

        for (Object[] item: listItems) {
            StatisticsTeacherDTO statisticsTeacherDTO;
            statisticsTeacherDTO = toStatisticsTeachersDTO(((String) item[0]), ((CourseTeacher) item[1]),
                    ((Integer) item[2]), ((Integer) item[3]), ((Integer) item[4]));

            statisticsTeacherDTOS.add(statisticsTeacherDTO);
        }

        return statisticsTeacherDTOS;
    }

    default StatisticsPersonalDTO toStatisticsPersonalDTO(Integer[] personalStatistics){
        if ( personalStatistics == null ) {
            return null;
        }

        return new StatisticsPersonalDTO(personalStatistics[0], personalStatistics[1],
                personalStatistics[2], personalStatistics[3], personalStatistics[4]);
    }

    default StatisticsGlobalDTO toStatisticsGlobalDTO(Integer[] globalStatistics){
        if ( globalStatistics == null ) {
            return null;
        }

        return new StatisticsGlobalDTO(globalStatistics[0], globalStatistics[1], globalStatistics[2],
                globalStatistics[3], globalStatistics[4], globalStatistics[5], globalStatistics[6], globalStatistics[7], globalStatistics[8]);
    }
}
