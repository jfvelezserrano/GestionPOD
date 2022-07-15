package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
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

    interface SubjectBase extends Subject.Base, Schedule.Base {
    }

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CourseService courseService;


    @GetMapping(value = "/titles")
    public ResponseEntity<List<String>> getTitles(){
        return new ResponseEntity<>(subjectService.getTitles(), HttpStatus.OK);
    }

    @RequestMapping(value = "/currentTitles")
    public ResponseEntity<List<String>> getTitlesCurrentCourse() {

        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()) {
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

    @JsonView(SubjectBase.class)
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object[]> getById(@PathVariable Long id){
        Optional<Subject> subjectOptional = subjectService.findById(id);
        if(subjectOptional.isPresent()){
            Optional<Course> course = courseService.findLastCourse();
            if (course.isPresent() && course.get().isSubjectInCourse(subjectOptional.get())) {
                List<String> teachers = subjectOptional.get().recordSubject().get(course.get().getName());
                Object[] obj = new Object[] { subjectOptional.get(), teachers};
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(SubjectBase.class)
    @GetMapping(value = "")
    public ResponseEntity<List<Subject>> findAllInCurrentCourse(){
        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()){
            return new ResponseEntity<>(subjectService.findByCourse(course.get().getId()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(SubjectBase.class)
    @GetMapping(value = "{id}/record")
    public ResponseEntity<Map<String, List<String>>> recordSubject(@PathVariable Long id){
        Optional<Subject> subjectOptional = subjectService.findById(id);
        if(subjectOptional.isPresent()){
            Map<String, List<String>> record = subjectOptional.get().recordSubject();
            return new ResponseEntity<>(record, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(SubjectBase.class)
    @GetMapping("/search")
    public ResponseEntity<List<Object[]>> search(@RequestParam(value="occupation", required = false, defaultValue = "") String occupation,
                                    @RequestParam(value="quarter", required = false, defaultValue = "") String quarter,
                                    @RequestParam(value="turn", required = false, defaultValue = "") String turn,
                                    @RequestParam(value="title", required = false, defaultValue = "") String title,
                                    @RequestParam(value="teacher", required = false, defaultValue = "-1") Long teacher,
                                    @RequestParam(defaultValue = "name") String typeSort) {

        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()) {
            Sort sort = Sort.by(typeSort).ascending();
            List<Object[]> list = subjectService.searchByCourse(course.get(), occupation, quarter, turn, title, teacher, sort);

            return new ResponseEntity<>(list, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
