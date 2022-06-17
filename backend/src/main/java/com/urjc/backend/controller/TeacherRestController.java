package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.*;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import com.urjc.backend.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teachers")
public class TeacherRestController {

    private static final Logger log = LoggerFactory.getLogger(CourseRestController.class);

    interface TeacherBaseWithRoles extends Teacher.Base, Teacher.Roles {
    }

    interface SubjectBase extends Subject.Base, Schedule.Base {
    }

    interface CourseBase extends Course.Base{
    }

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;


    @JsonView(TeacherBaseWithRoles.class)
    @PutMapping("/admin")
    public ResponseEntity<?> updateRole(@RequestBody Teacher teacher) {

        Teacher teacherIfExists = teacherService.getTeacherIfExists(teacher);
        if(teacherIfExists != null) {
            Teacher teacherDDBB = teacherService.findIfIsInCurrentCourse(teacherIfExists.getEmail());
            if (teacherDDBB != null || (teacherIfExists.getRoles().contains("ADMIN") && teacherIfExists.getRoles() != teacher.getRoles())) {
                teacherIfExists.setRoles(teacher.getRoles());
                teacherService.save(teacherIfExists);
                return new ResponseEntity<>(teacherIfExists, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(TeacherBaseWithRoles.class)
    @GetMapping(value = "")
    public ResponseEntity<List<Teacher>> findAllInCurrentCourse(@RequestParam(value = "role", required = false) String role){
        if(role == null){
            return new ResponseEntity<>(teacherService.findAllInCurrentCourse(), HttpStatus.OK);
        }

        List<Teacher> admins = teacherService.findAllByRole(role);

        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @PostMapping("/join/{idSubject}")
    public ResponseEntity<?> joinSubject(@RequestBody TeacherRequest teacherRequest, @PathVariable Long idSubject) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Teacher teacher = teacherService.findByEmail(authentication.getName());

        Optional<Subject> subject = subjectService.findById(idSubject);

        if(subject.isPresent()){
            Course course = courseService.findLastCourse();

            if (course != null && course.isSubjectInCourse(subject.get())) {
                POD pod = teacher.hasSubjectInCourse(subject.get(), course);
                if(pod != null){
                    pod.setChosenHours(teacherRequest.getHours());
                }else{
                    teacher.addChosenSubject(subject.get(), course, teacherRequest.getHours());
                }
                teacherService.save(teacher);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/unjoin/{idSubject}")
    public ResponseEntity<?> unjoinSubject(@PathVariable Long idSubject) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Teacher teacher = teacherService.findByEmail(authentication.getName());

        Optional<Subject> subject = subjectService.findById(idSubject);

        if(subject.isPresent()){
            Course course = courseService.findLastCourse();

            if (course != null && course.isSubjectInCourse(subject.get())) {
                POD pod = teacher.hasSubjectInCourse(subject.get(), course);
                if(pod != null){
                    teacher.deleteChosenSubject(pod);
                }
                teacherService.save(teacher);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(SubjectBase.class)
    @GetMapping(value = "/mySubjects")
    public ResponseEntity<?> findAllMySubjects(@RequestParam(defaultValue = "name") String typeSort){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Course course = courseService.findLastCourse();
        if (course != null) {
            Sort sort = Sort.by(typeSort).ascending();
            List<Object[]> mySubjects = subjectService.findMySubjects(teacher.getId(), course, sort);
            return new ResponseEntity<>(mySubjects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(CourseBase.class)
    @GetMapping(value = "/myCourses")
    public ResponseEntity<List<Course>> findAllMyCourses(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Course course = courseService.findLastCourse();
        if (course != null) {
            List<Course> myCourses = courseService.getCoursesByTeacher(teacher.getId());
            return new ResponseEntity<>(myCourses, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
