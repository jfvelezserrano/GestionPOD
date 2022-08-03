package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;


    @GetMapping(value = "/myData")
    public ResponseEntity<Object[]> findPersonalStatistics(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            Teacher teacher = teacherService.findByEmail(authentication.getName());
            Optional<Course> course = courseService.findLastCourse();
            if (course.isPresent()) {
                Object[] myStatistics = teacherService.findPersonalStatistics(teacher.getId(), course.get());
                return new ResponseEntity<>(myStatistics, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/myMates")
    public ResponseEntity<List<Object[]>> findMates(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            Teacher teacher = teacherService.findByEmail(authentication.getName());
            Optional<Course> course = courseService.findLastCourse();
            if (course.isPresent()) {
                List<Object[]>  myStatistics = teacherService.findMates(teacher.getId(), course.get().getId());
                return new ResponseEntity<>(myStatistics, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/mySubjects/{idCourse}")
    public ResponseEntity<List<Object[]>> findMySubjectsByCourse(@PathVariable Long idCourse){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Optional<Course> course = courseService.findById(idCourse);
        if (course.isPresent()) {
            Sort sort = Sort.by("name").ascending();
            List<Object[]> mySubjects = subjectService.findNameAndQuarterByTeacherAndCourse(teacher.getId(), course.get(), sort);
            return new ResponseEntity<>(mySubjects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/myHoursPerSubject")
    public ResponseEntity<List<Object[]>> graphHoursPerSubject(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Sort sort = Sort.by("name").ascending();
            List<Object[]> subjects = subjectService.hoursPerSubjectByTeacherAndCourse(teacher, course.get(), sort);
            return new ResponseEntity<>(subjects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/myPercentageHoursSubjects")
    public ResponseEntity<List<Object[]>> graphPercentageHoursSubjects(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Sort sort = Sort.by("name").ascending();
            List<Object[]> subjects = subjectService.percentageHoursByTeacherAndCourse(teacher, course.get(), sort);
            return new ResponseEntity<>(subjects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/teachersStatistics")
    public ResponseEntity<List<Object[]>> getTeachersStatistics(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "name") String typeSort) {

        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Pageable pageable = PageRequest.of(page, 12, Sort.by(typeSort).ascending());
            List<Object[]> teachersStatistics = teacherService.allTeachersStatistics(course.get(), pageable);
            return new ResponseEntity<>(teachersStatistics, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "")
    public ResponseEntity<Object[]> getGlobalStatistics(){
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Object[] myStatistics = courseService.getGlobalStatistics(course.get());
            return new ResponseEntity<>(myStatistics, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
