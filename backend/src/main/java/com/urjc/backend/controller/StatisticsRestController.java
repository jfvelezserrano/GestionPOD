package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.dto.*;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.mapper.IStatisticsMapper;
import com.urjc.backend.mapper.ISubjectMapper;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.urjc.backend.error.ErrorMessageConstants.NOT_FOUND_ID_COURSE;
import static com.urjc.backend.error.ErrorMessageConstants.NO_COURSE_YET;

@RestController
@RequestMapping(value = "/api/statistics")
public class StatisticsRestController {

    interface SubjectNameAndQuarter extends SubjectDTO.NameAndQuarter {
    }

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    IStatisticsMapper statisticsMapper;

    @Autowired
    ISubjectMapper subjectMapper;


    @GetMapping(value = "/myData", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatisticsPersonalDTO> findPersonalStatistics() {
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());
            Integer[] myStatistics = teacherService.findPersonalStatistics(teacher.getId(), course.get());
            return new ResponseEntity<>(statisticsMapper.toStatisticsPersonalDTO(myStatistics), HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @GetMapping(value = "/myMates", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StatisticsMatesDTO>> findMates() {
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());
            List<Object[]> myStatistics = teacherService.findMates(teacher.getId(), course.get().getId());

            return new ResponseEntity<>(statisticsMapper.listStatisticsMatesDTO(myStatistics), HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @JsonView(SubjectNameAndQuarter.class)
    @GetMapping(value = "/mySubjects/{idCourse}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubjectDTO>> findMySubjectsByCourse(@PathVariable Long idCourse) {
        Optional<Course> course = courseService.findById(idCourse);
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());
            Sort sort = Sort.by("name").ascending();
            List<Subject> mySubjects = subjectService.findByCourseAndTeacher(teacher.getId(), course.get(), sort);
            return new ResponseEntity<>(subjectMapper.listSubjectDTO(mySubjects), HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NOT_FOUND_ID_COURSE + idCourse);
    }

    @GetMapping(value = "/myHoursPerSubject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StatisticsGraphHoursDTO>> graphHoursPerSubject() {
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());
            Sort sort = Sort.by("name").ascending();
            List<Object[]> statistics = subjectService.hoursPerSubjectByTeacherAndCourse(teacher, course.get(), sort);
            return new ResponseEntity<>(statisticsMapper.listStatisticsGraphHoursDTO(statistics), HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @GetMapping(value = "/myPercentageHoursSubjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StatisticsGraphPercentageDTO>> graphPercentageHoursSubjects() {
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());
            Sort sort = Sort.by("name").ascending();
            List<Object[]> statistics = subjectService.percentageHoursByTeacherAndCourse(teacher, course.get(), sort);
            return new ResponseEntity<>(statisticsMapper.listStatisticsGraphPercentageDTO(statistics), HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @GetMapping(value = "/teachersStatistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StatisticsTeacherDTO>> getTeachersStatistics(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                            @RequestParam(defaultValue = "name") String typeSort) {

        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Pageable pageable = PageRequest.of(page, 12, Sort.by(typeSort).ascending());
            List<Object[]> teachersStatistics = teacherService.allTeachersStatistics(course.get(), pageable);
            List<StatisticsTeacherDTO> statisticsTeacherDTOS = statisticsMapper.listStatisticsTeachersDTO(teachersStatistics);
            return new ResponseEntity<>(statisticsTeacherDTOS, HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatisticsGlobalDTO> getGlobalStatistics() {
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Integer[] statistics = courseService.getGlobalStatistics(course.get());
            return new ResponseEntity<>(statisticsMapper.toStatisticsGlobalDTO(statistics), HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }
}
