package com.urjc.backend.mapper;

import com.urjc.backend.dto.*;
import com.urjc.backend.model.CourseTeacher;
import com.urjc.backend.model.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IStatisticsMapper {
    IStatisticsMapper INSTANCE = Mappers.getMapper(IStatisticsMapper.class);

    StatisticsMatesDTO toStatisticsMatesDTO(String mateName, String mateSubject, Integer matePercentage);

    default List<StatisticsMatesDTO> listStatisticsMatesDTO(List<Object[]> listItems){
        if ( listItems == null ) {
            return null;
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
            return null;
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
            return null;
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
            return null;
        }

        List<StatisticsTeacherDTO> statisticsTeacherDTOS = new ArrayList<>();

        for (Object[] item: listItems) {
            StatisticsTeacherDTO statisticsTeacherDTO;
            statisticsTeacherDTO = toStatisticsTeachersDTO(((String) item[0]), ((CourseTeacher) item[1]),
                    ((Long) item[2]).intValue(), ((Long) item[3]).intValue(), ((Long) item[4]).intValue());

            statisticsTeacherDTOS.add(statisticsTeacherDTO);
        }

        return statisticsTeacherDTOS;
    }

    default StatisticsPersonalDTO toStatisticsPersonalDTO(Integer[] personalStatistics){
        if ( personalStatistics == null ) {
            return null;
        }

        StatisticsPersonalDTO statisticsPersonalDTO = new StatisticsPersonalDTO();

        statisticsPersonalDTO.setPercentage(personalStatistics[0]);
        statisticsPersonalDTO.setCharge(personalStatistics[1]);
        statisticsPersonalDTO.setCorrectedHours(personalStatistics[2]);
        statisticsPersonalDTO.setNumSubjects(personalStatistics[3]);
        statisticsPersonalDTO.setNumConflicts(personalStatistics[4]);

        return statisticsPersonalDTO;
    }

    default StatisticsGlobalDTO toStatisticsGlobalDTO(Integer[] globalStatistics){
        if ( globalStatistics == null ) {
            return null;
        }

        StatisticsGlobalDTO statisticsGlobalDTO = new StatisticsGlobalDTO();

        statisticsGlobalDTO.setPercentageCharge(globalStatistics[0]);
        statisticsGlobalDTO.setTotalChosenHours(globalStatistics[1]);
        statisticsGlobalDTO.setTotalCharge(globalStatistics[2]);
        statisticsGlobalDTO.setPercentageForce(globalStatistics[3]);
        statisticsGlobalDTO.setTotalCorrectHours(globalStatistics[4]);
        statisticsGlobalDTO.setPercentageCompletations(globalStatistics[5]);
        statisticsGlobalDTO.setNumCompletations(globalStatistics[6]);
        statisticsGlobalDTO.setNumSubjects(globalStatistics[7]);
        statisticsGlobalDTO.setNumConflicts(globalStatistics[8]);


        return statisticsGlobalDTO;
    }
}
