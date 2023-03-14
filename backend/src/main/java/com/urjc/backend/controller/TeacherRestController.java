package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.dto.*;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.error.exception.RedirectException;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.urjc.backend.error.ErrorMessageConstants.NO_COURSE_YET;

@RestController
@RequestMapping(value = "/api/teachers")
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
    private ITeacherMapper teacherMapper;

    @Autowired
    private ISubjectMapper subjectMapper;

    @Autowired
    private ICourseMapper courseMapper;

    @PutMapping(value = "/role", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherDTO> updateRole(@RequestBody @Valid TeacherDTO teacherDTO) {
        Teacher teacher = teacherMapper.toTeacher(teacherDTO);
        Teacher teacherDDBB = teacherService.findByEmail(teacher.getEmail());
        if(teacherDDBB != null && !teacherDDBB.getEmail().equals(emailMainAdmin)) {
            Teacher teacherInCurrentCourse = teacherService.findIfIsInCurrentCourse(teacherDDBB.getEmail());
            if (teacherInCurrentCourse != null) {
                teacherDDBB.setRoles(teacher.getRoles());
                teacherService.save(teacherDDBB);

                return new ResponseEntity<>(teacherMapper.toTeacherDTO(teacherDDBB), HttpStatus.OK);
            }
        }
        throw new GlobalException(HttpStatus.BAD_REQUEST, "El docente que se quiere actualizar es incorrecto o no puede actualizarse");
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeacherDTO>> findAllByRole(@RequestParam(value = "role", required = false) String role){
        Optional<Course> course = courseService.findLastCourse();
        if (role == null && course.isPresent()) {
            List<Teacher> teachers = teacherService.findAllByCourse(course.get().getId(), Pageable.unpaged());
            return new ResponseEntity<>(teacherMapper.map(teachers), HttpStatus.OK);
        } else if(role == null){
            role = "ADMIN";
        }

        List<Teacher> teachersWithRole = teacherService.findAllByRole(role);
        return new ResponseEntity<>(teacherMapper.map(teachersWithRole), HttpStatus.OK);
    }

    @PostMapping("/join/{idSubject}")
    public ResponseEntity<Void> joinSubject(@RequestBody @Valid TeacherJoinSubjectDTO teacherJoinSubjectDTO, @PathVariable Long idSubject) {
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
            throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, "No existe esa asignatura");
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
            throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
        }
        throw new RedirectException(HttpStatus.NOT_FOUND, "No se ha encontrado la asignatura con id " + idSubject);
    }

    @JsonView(SubjectTeacherDTOStatusAndConflicts.class)
    @GetMapping(value = "/mySubjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubjectTeacherDTO>> findAllMySubjects(@RequestParam(defaultValue = "name") String typeSort){
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());

            Sort sort = Sort.by(typeSort).ascending();
            List<Object[]> mySubjects = subjectService.findConflictsByTeacherAndCourse(teacher.getId(), course.get(), sort);

            List<SubjectTeacherDTO> subjectTeacherDTOs = subjectMapper.listSubjectTeacherDTOs(mySubjects);
            return new ResponseEntity<>(subjectTeacherDTOs, HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @GetMapping(value = "/myCourses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CourseDTO>> findAllMyCourses(){
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());

            List<Course> myCourses = courseService.findCoursesTakenByTeacher(teacher.getId());
            return new ResponseEntity<>(courseMapper.map(myCourses), HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @GetMapping(value = "/myEditableData", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseTeacherDTO> getEditableData(){
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object[] myEditableData = teacherService.getEditableData(authentication.getName(), course.get());

            CourseTeacherDTO courseTeacherDTO = teacherMapper.toTeacherEditableDataDTO(((Integer) myEditableData[0]), ((String) myEditableData[1]));
            return new ResponseEntity<>(courseTeacherDTO, HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @PutMapping("/myEditableData")
    public ResponseEntity<Void> editData(@RequestBody @Valid CourseTeacherDTO courseTeacherDTO){
        Optional<Course> course = courseService.findLastCourse();
        if (course.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Teacher teacher = teacherService.findByEmail(authentication.getName());

            teacher.editEditableData(course.get(), courseTeacherDTO.getCorrectedHours(), courseTeacherDTO.getObservation());
            teacherService.save(teacher);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }
}
