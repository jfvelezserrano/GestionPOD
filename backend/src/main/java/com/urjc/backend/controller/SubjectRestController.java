package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.*;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/subjects")
public class SubjectRestController {

    interface SubjectDetails extends Subject.Base, Schedule.Base, POD.Base, Teacher.Base {
    }

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
            Course course = courseService.findLastCourse();
            if (course != null && course.isSubjectInCourse(subjectOptional.get())) {
                List<String> teachers = subjectService.recordSubject(subjectOptional.get()).get(course.getName());

                Object[] obj = new Object[] { subjectOptional.get(), teachers};
                return new ResponseEntity<>(obj, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(SubjectBase.class)
    @GetMapping(value = "")
    public ResponseEntity<List<Subject>> findAllInCurrentCourse(){
        Course course = courseService.findLastCourse();
        if(course == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(subjectService.findAllInCurrentCourse(course.getId()), HttpStatus.OK);
    }

    @JsonView(SubjectBase.class)
    @GetMapping(value = "{id}/record")
    public ResponseEntity<Map<String, List<String>>> recordSubject(@PathVariable Long id){
        Optional<Subject> subjectOptional = subjectService.findById(id);
        if(subjectOptional.isPresent()){
            Map<String, List<String>> record = subjectService.recordSubject(subjectOptional.get());
            return new ResponseEntity<>(record, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
