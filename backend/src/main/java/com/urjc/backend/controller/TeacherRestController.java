package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.dto.*;
import com.urjc.backend.mapper.ICourseMapper;
import com.urjc.backend.mapper.ISubjectMapper;
import com.urjc.backend.mapper.ITeacherMapper;
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

    interface SubjectTeacherDTOStatusAndConflicts extends SubjectTeacherDTO.Base, SubjectTeacherDTO.Status,
            SubjectTeacherDTO.Conflicts {
    }

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    ITeacherMapper teacherMapper;

    @Autowired
    ISubjectMapper subjectMapper;

    @Autowired
    ICourseMapper courseMapper;

    @PutMapping("/role")
    public ResponseEntity<TeacherDTO> updateRole(@RequestBody Teacher teacher) {

        Teacher teacherIfExists = teacherService.getTeacherIfExists(teacher);
        if(teacherIfExists != null) {
            Teacher teacherDDBB = teacherService.findIfIsInCurrentCourse(teacherIfExists.getEmail());
            if (teacherDDBB != null || (teacherIfExists.getRoles().contains("ADMIN") && teacherIfExists.getRoles() != teacher.getRoles())) {
                teacherIfExists.setRoles(teacher.getRoles());
                teacherService.save(teacherIfExists);

                TeacherDTO teacherDTO = teacherMapper.toTeacherDTO(teacherIfExists);
                return new ResponseEntity<>(teacherDTO, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "")
    public ResponseEntity<List<TeacherDTO>> findAllByRole(@RequestParam(value = "role", required = false) String role){
        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()) {
            if (role == null) {
                List<Teacher> teachers = teacherService.findAllByCourse(course.get().getId(), Pageable.unpaged());

                return new ResponseEntity<>(teacherMapper.map(teachers), HttpStatus.OK);
            } else {
                List<Teacher> teachersWithRole = teacherService.findAllByRole(role);

                return new ResponseEntity<>(teacherMapper.map(teachersWithRole), HttpStatus.OK);
            }
        }
        else{ return new ResponseEntity<>(HttpStatus.NOT_FOUND); }
    }

    @PostMapping("/join/{idSubject}")
    public ResponseEntity<Void> joinSubject(@RequestBody TeacherRequestDTO teacherRequest, @PathVariable Long idSubject) {

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
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/unjoin/{idSubject}")
    public ResponseEntity<Void> unjoinSubject(@PathVariable Long idSubject) {

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
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(SubjectTeacherDTOStatusAndConflicts.class)
    @GetMapping(value = "/mySubjects")
    public ResponseEntity<List<SubjectTeacherDTO>> findAllMySubjects(@RequestParam(defaultValue = "name") String typeSort){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Sort sort = Sort.by(typeSort).ascending();
            List<Object[]> mySubjects = subjectService.findByTeacherAndCourse(teacher.getId(), course.get(), sort);

            List<SubjectTeacherDTO> subjectTeacherDTOs = subjectMapper.listSubjectTeacherDTOs(mySubjects);
            return new ResponseEntity<>(subjectTeacherDTOs, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/myCourses")
    public ResponseEntity<List<CourseDTO>> findAllMyCourses(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            List<Course> myCourses = courseService.findByTeacherOrderByCreationDate(teacher.getId());
            return new ResponseEntity<>(courseMapper.map(myCourses), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/myEditableData")
    public ResponseEntity<CourseTeacherDTO> getEditableData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Object[] myEditableData = teacherService.getEditableData(authentication.getName(), course.get());

            CourseTeacherDTO courseTeacherDTO = teacherMapper.toTeacherEditableDataDTO(((Integer) myEditableData[0]), ((String) myEditableData[1]));
            return new ResponseEntity<>(courseTeacherDTO, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/myEditableData")
    public ResponseEntity<Void> editData(@RequestBody CourseTeacherDTO courseTeacherDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Teacher teacher = teacherService.findByEmail(authentication.getName());
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            teacher.editEditableData(course.get(), courseTeacherDTO.getCorrectedHours(), courseTeacherDTO.getObservation());
            teacherService.save(teacher);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
