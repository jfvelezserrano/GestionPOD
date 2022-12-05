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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teachers")
public class TeacherRestController {

    interface SubjectTeacherDTOStatusAndConflicts extends SubjectTeacherDTO.Base, SubjectTeacherDTO.Status,
            SubjectTeacherDTO.Conflicts {
    }

    @Value("${email.main.admin}")
    private String emailMainAdmin;

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
    public ResponseEntity<TeacherDTO> updateRole(@RequestBody @Valid TeacherDTO teacherDTO) {

        Teacher teacher = teacherMapper.toTeacher(teacherDTO);
        Teacher teacherIfExists = teacherService.findByEmail(teacher.getEmail());
        if(teacherIfExists != null && !teacherIfExists.getEmail().equals(emailMainAdmin)) {
            Teacher teacherDDBB = teacherService.findIfIsInCurrentCourse(teacherIfExists.getEmail());
            if (teacherDDBB != null || (teacherIfExists.getRoles().contains("ADMIN") && teacherIfExists.getRoles() != teacher.getRoles())) {
                teacherIfExists.setRoles(teacher.getRoles());
                teacherService.save(teacherIfExists);

                return new ResponseEntity<>(teacherMapper.toTeacherDTO(teacherIfExists), HttpStatus.OK);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El docente que se quiere actualizar es incorrecto o no puede actualizarse");
    }

    @GetMapping(value = "")
    public ResponseEntity<List<TeacherDTO>> findAllByRole(@RequestParam(value = "role", required = false) String role){
        Optional<Course> course = courseService.findLastCourse();
        if (role == null && course.isPresent()) {
            List<Teacher> teachers = teacherService.findAllByCourse(course.get().getId(), Pageable.unpaged());
            return new ResponseEntity<>(teacherMapper.map(teachers), HttpStatus.OK);
        } else if (role != null){
            List<Teacher> teachersWithRole = teacherService.findAllByRole(role);
            return new ResponseEntity<>(teacherMapper.map(teachersWithRole), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay ningún curso aún");
    }

    @PostMapping("/join/{idSubject}")
    public ResponseEntity<Void> joinSubject(@RequestBody TeacherJoinSubjectDTO teacherJoinSubjectDTO, @PathVariable Long idSubject) {

        Optional<Subject> subject = subjectService.findById(idSubject);

        if(subject.isPresent()){
            Optional<Course> course = courseService.findLastCourse();

            if (course.isPresent() && course.get().isSubjectInCourse(subject.get())) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Teacher teacher = teacherService.findByEmail(authentication.getName());

                POD pod = teacher.hasSubjectInCourse(subject.get(), course.get());
                if(pod != null){
                    pod.setChosenHours(teacherJoinSubjectDTO.getHours());
                }else{
                    teacher.addChosenSubject(subject.get(), course.get(), teacherJoinSubjectDTO.getHours());
                }
                teacherService.save(teacher);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay ningún curso aún");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe esa asignatura");
    }

    @DeleteMapping("/unjoin/{idSubject}")
    public ResponseEntity<Void> unjoinSubject(@PathVariable Long idSubject) {

        Optional<Subject> subject = subjectService.findById(idSubject);

        if(subject.isPresent()){
            Optional<Course> course = courseService.findLastCourse();

            if (course.isPresent() && course.get().isSubjectInCourse(subject.get())) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Teacher teacher = teacherService.findByEmail(authentication.getName());

                POD pod = teacher.hasSubjectInCourse(subject.get(), course.get());
                if(pod != null){
                    teacher.deleteChosenSubject(pod);
                }
                teacherService.save(teacher);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay ningún curso aún");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, " ");
    }

    @JsonView(SubjectTeacherDTOStatusAndConflicts.class)
    @GetMapping(value = "/mySubjects")
    public ResponseEntity<List<SubjectTeacherDTO>> findAllMySubjects(@RequestParam(defaultValue = "name") String typeSort){

        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());

            Sort sort = Sort.by(typeSort).ascending();
            List<Object[]> mySubjects = subjectService.findByTeacherAndCourse(teacher.getId(), course.get(), sort);

            List<SubjectTeacherDTO> subjectTeacherDTOs = subjectMapper.listSubjectTeacherDTOs(mySubjects);
            return new ResponseEntity<>(subjectTeacherDTOs, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay ningún curso aún");
    }

    @GetMapping(value = "/myCourses")
    public ResponseEntity<List<CourseDTO>> findAllMyCourses(){

        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());

            List<Course> myCourses = courseService.findByTeacherOrderByCreationDate(teacher.getId());
            return new ResponseEntity<>(courseMapper.map(myCourses), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay ningún curso aún");
    }

    @GetMapping(value = "/myEditableData")
    public ResponseEntity<CourseTeacherDTO> getEditableData(){

        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object[] myEditableData = teacherService.getEditableData(authentication.getName(), course.get());

            CourseTeacherDTO courseTeacherDTO = teacherMapper.toTeacherEditableDataDTO(((Integer) myEditableData[0]), ((String) myEditableData[1]));
            return new ResponseEntity<>(courseTeacherDTO, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay ningún curso aún");
    }

    @PutMapping(value = "/myEditableData")
    public ResponseEntity<Void> editData(@RequestBody @Valid CourseTeacherDTO courseTeacherDTO){

        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());

            teacher.editEditableData(course.get(), courseTeacherDTO.getCorrectedHours(), courseTeacherDTO.getObservation());
            teacherService.save(teacher);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay ningún curso aún");
    }
}
