package com.urjc.backend.controller;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping(value = "")
    public ResponseEntity<Object[]> findPersonalStatistics(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            Teacher teacher = teacherService.findByEmail(authentication.getName());
            Course course = courseService.findLastCourse();
            if (course != null) {
                Object[] myStatistics = teacherService.findPersonalStatistics(teacher.getId(), course);
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
            Course course = courseService.findLastCourse();
            if (course != null) {
                List<Object[]>  myStatistics = teacherService.findMates(teacher.getId(), course.getId());
                return new ResponseEntity<>(myStatistics, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/mySubjects/{idCourse}")
    public ResponseEntity<Object[]> findMySubjectsByCourse(@PathVariable Long idCourse){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Optional<Course> course = courseService.findById(idCourse);
        if (course.isPresent()) {
            Sort sort = Sort.by("name").ascending();
            Object[] mySubjects = subjectService.findMySubjectsByCourse(teacher.getId(), course.get(), sort);
            return new ResponseEntity<>(mySubjects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/hoursPerSubject")
    public ResponseEntity<List<Object[]>> graphHoursPerSubject(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Course course = courseService.findLastCourse();
        if (course != null) {
            Sort sort = Sort.by("name").ascending();
            List<Object[]> subjects = subjectService.graphHoursPerSubject(teacher, course, sort);
            return new ResponseEntity<>(subjects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/percentageHoursSubjects")
    public ResponseEntity<List<Object[]>> graphPercentageHoursSubjects(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Course course = courseService.findLastCourse();
        if (course != null) {
            Sort sort = Sort.by("name").ascending();
            List<Object[]> subjects = subjectService.graphPercentageHoursSubjects(teacher, course, sort);
            return new ResponseEntity<>(subjects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
