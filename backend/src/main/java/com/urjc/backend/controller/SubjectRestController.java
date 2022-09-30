package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.dto.SubjectDTO;
import com.urjc.backend.dto.SubjectTeacherDTO;
import com.urjc.backend.mapper.ISubjectMapper;
import com.urjc.backend.model.*;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/subjects")
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


    @GetMapping(value = "/titles")
    public ResponseEntity<List<String>> getTitles(){
        return new ResponseEntity<>(subjectService.getTitles(), HttpStatus.OK);
    }

    @GetMapping(value = "/currentTitles")
    public ResponseEntity<List<String>> getTitlesCurrentCourse() {

        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()){
             return new ResponseEntity<>(subjectService.getTitlesByCourse(course.get().getId()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/campus")
    public ResponseEntity<List<String>> getCampus(){
        return new ResponseEntity<>(subjectService.getCampus(), HttpStatus.OK);
    }

    @GetMapping(value = "/types")
    public ResponseEntity<List<String>> getTypes(){
        return new ResponseEntity<>(subjectService.getTypes(), HttpStatus.OK);
    }

    @JsonView(SubjectTeacherDTOBase.class)
    @GetMapping(value = "/{id}")
    public ResponseEntity<SubjectTeacherDTO> getByIdInCurrentCourse(@PathVariable Long id){
        Optional<Subject> subjectOptional = subjectService.findById(id);
        if(subjectOptional.isPresent()){
            Optional<Course> course = courseService.findLastCourse();
            if (course.isPresent() && course.get().isSubjectInCourse(subjectOptional.get())) {
                List<String> teachers = subjectOptional.get().recordSubject().get(course.get().getName());

                SubjectTeacherDTO subjectTeacherDTO = subjectMapper.toSubjectTeacherDTO(subjectOptional.get(), teachers, 0, null);

                return new ResponseEntity<>(subjectTeacherDTO, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "")
    public ResponseEntity<List<SubjectDTO>> findAllInCurrentCourse(){
        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()){
            List<Subject> subjects = subjectService.findByCourse(course.get().getId());
            List<SubjectDTO> subjectDTOS = subjectMapper.listSubjectDTO(subjects);
            return new ResponseEntity<>(subjectDTOS, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "{id}/record")
    public ResponseEntity<Map<String, List<String>>> recordSubject(@PathVariable Long id){
        Optional<Subject> subjectOptional = subjectService.findById(id);
        if(subjectOptional.isPresent()){
            Map<String, List<String>> record = subjectOptional.get().recordSubject();
            return new ResponseEntity<>(record, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(SubjectTeacherDTOStatus.class)
    @GetMapping("/search")
    public ResponseEntity<List<SubjectTeacherDTO>> search(@RequestParam(value="occupation", required = false, defaultValue = "") String occupation,
                                    @RequestParam(value="quarter", required = false, defaultValue = "") String quarter,
                                    @RequestParam(value="turn", required = false, defaultValue = "") String turn,
                                    @RequestParam(value="title", required = false, defaultValue = "") String title,
                                    @RequestParam(value="teacher", required = false, defaultValue = "-1") Long teacher,
                                    @RequestParam(defaultValue = "name") String typeSort) {

        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()) {
            Sort sort = Sort.by(typeSort).ascending();
            List<Object[]> list = subjectService.searchByCourse(course.get(), occupation, quarter, turn, title, teacher, sort);

            List<SubjectTeacherDTO> subjectTeacherDTOS = subjectMapper.listSubjectTeacherDTOs(list);
            return new ResponseEntity<>(subjectTeacherDTOS, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
