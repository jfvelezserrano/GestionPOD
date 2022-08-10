package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.*;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/teachers")
public class TeacherRestController {

    interface TeacherBaseWithRoles extends Teacher.Base, Teacher.Roles {
    }

    interface TeacherDataToEdit extends Teacher.Base, Teacher.DataToEdit, CourseTeacher.DataToEdit {
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
    public ResponseEntity<List<Teacher>> findAllByRole(@RequestParam(value = "role", required = false) String role){
        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()) {
            if (role == null) {
                return new ResponseEntity<>(teacherService.findAllByCourse(course.get().getId(), Pageable.unpaged()), HttpStatus.OK);
            } else {
                List<Teacher> teachersWithRole = teacherService.findAllByRole(role);
                return new ResponseEntity<>(teachersWithRole, HttpStatus.OK);
            }
        }
        else{ return new ResponseEntity<>(HttpStatus.NOT_FOUND); }
    }

    @PostMapping("/join/{idSubject}")
    public ResponseEntity<?> joinSubject(@RequestBody TeacherRequest teacherRequest, @PathVariable Long idSubject) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Teacher teacher = teacherService.findByEmail(authentication.getName());

        Optional<Subject> subject = subjectService.findById(idSubject);

        if(subject.isPresent()){
            Optional<Course> course = courseService.findLastCourse();

            if (course.isPresent() && course.get().isSubjectInCourse(subject.get())) {
                POD pod = teacher.hasSubjectInCourse(subject.get(), course.get());
                if(pod != null){
                    pod.setChosenHours(teacherRequest.getHours());
                }else{
                    teacher.addChosenSubject(subject.get(), course.get(), teacherRequest.getHours());
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
            Optional<Course> course = courseService.findLastCourse();

            if (course.isPresent() && course.get().isSubjectInCourse(subject.get())) {
                POD pod = teacher.hasSubjectInCourse(subject.get(), course.get());
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
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Sort sort = Sort.by(typeSort).ascending();
            List<Object[]> mySubjects = subjectService.findByTeacherAndCourse(teacher.getId(), course.get(), sort);
            return new ResponseEntity<>(mySubjects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(CourseBase.class)
    @GetMapping(value = "/myCourses")
    public ResponseEntity<List<Course>> findAllMyCourses(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            List<Course> myCourses = courseService.findByTeacherOrderByCreationDate(teacher.getId());
            return new ResponseEntity<>(myCourses, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(TeacherDataToEdit.class)
    @GetMapping(value = "/myEditableData")
    public ResponseEntity<Object[]> getEditableData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Object[] myEditableData = teacherService.getEditableData(authentication.getName(), course.get());
            return new ResponseEntity<>(myEditableData, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/myEditableData")
    public ResponseEntity<Teacher> editData(@RequestBody EditableDataRequest editableDataRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            teacher.editEditableData(course.get(), editableDataRequest.getCorrectedHours(), editableDataRequest.getObservation());
            teacherService.save(teacher);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
