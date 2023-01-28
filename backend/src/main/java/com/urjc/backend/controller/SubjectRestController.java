package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.dto.SubjectDTO;
import com.urjc.backend.dto.SubjectTeacherDTO;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.error.exception.RedirectException;
import com.urjc.backend.mapper.ISubjectMapper;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.urjc.backend.error.ErrorMessageConstants.NO_COURSE_YET;

@RestController
@RequestMapping(value = "/api/subjects")
public class SubjectRestController {

    interface SubjectTeacherDTOStatus extends SubjectTeacherDTO.Base, SubjectTeacherDTO.Status {
    }

    interface SubjectTeacherDTOBase extends SubjectTeacherDTO.Base{
    }

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CourseService courseService;

    @Autowired
    ISubjectMapper subjectMapper;


    @GetMapping(value = "/titles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getTitles(){
        return new ResponseEntity<>(subjectService.getTitles(), HttpStatus.OK);
    }

    @GetMapping(value = "/currentTitles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getTitlesCurrentCourse() {

        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()){
             return new ResponseEntity<>(subjectService.getTitlesByCourse(course.get().getId()), HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @GetMapping(value = "/campus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getCampus(){
        return new ResponseEntity<>(subjectService.getCampus(), HttpStatus.OK);
    }

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getTypes(){
        return new ResponseEntity<>(subjectService.getTypes(), HttpStatus.OK);
    }

    @JsonView(SubjectTeacherDTOBase.class)
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubjectTeacherDTO> getByIdInCurrentCourse(@PathVariable Long id){
        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()){
            Optional<Subject> subject = subjectService.findById(id);
            if (subject.isPresent() && course.get().isSubjectInCourse(subject.get())) {
                List<String> teachers = subject.get().recordSubject().get(course.get().getName());

                SubjectTeacherDTO subjectTeacherDTO = subjectMapper.toSubjectTeacherDTO(subject.get(), teachers, 0, null);

                return new ResponseEntity<>(subjectTeacherDTO, HttpStatus.OK);
            }
            throw new GlobalException(HttpStatus.NOT_FOUND, "No existe esa asignatura");
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubjectDTO>> findAllInCurrentCourse(){
        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()){
            List<Subject> subjects = subjectService.findByCourse(course.get().getId());
            List<SubjectDTO> subjectDTOS = subjectMapper.listSubjectDTO(subjects);
            return new ResponseEntity<>(subjectDTOS, HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }

    @GetMapping(value = "{id}/record", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<String>>> recordSubject(@PathVariable Long id){
        Optional<Subject> subjectOptional = subjectService.findById(id);
        if(subjectOptional.isPresent()){
            Map<String, List<String>> mapRecord = subjectOptional.get().recordSubject();
            return new ResponseEntity<>(mapRecord, HttpStatus.OK);
        }
        throw new RedirectException(HttpStatus.NOT_FOUND, "No se ha encontrado la asignatura con id " + id);
    }

    @JsonView(SubjectTeacherDTOStatus.class)
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SubjectTeacherDTO>> search(@RequestParam(value="occupation", required = false, defaultValue = "") String occupation,
                                    @RequestParam(value="quarter", required = false, defaultValue = "") String quarter,
                                    @RequestParam(value="turn", required = false, defaultValue = "") Character turn,
                                    @RequestParam(value="title", required = false, defaultValue = "") String title,
                                    @RequestParam(value="teacher", required = false, defaultValue = "") String teacher,
                                    @RequestParam(defaultValue = "name") String typeSort) {

        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()) {
            Sort sort = Sort.by(typeSort).ascending();
            List<Object[]> list = subjectService.searchByCourse(course.get(), occupation, quarter, turn, title, teacher, sort);

            List<SubjectTeacherDTO> subjectTeacherDTOS = subjectMapper.listSubjectTeacherDTOs(list);
            return new ResponseEntity<>(subjectTeacherDTOS, HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }
}
