package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.Course;
import com.urjc.backend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectRestController {

    @Autowired
    private SubjectService subjectService;


    @GetMapping(value = "/titles")
    public ResponseEntity<List<String>> getTitles(){
        return new ResponseEntity<>(subjectService.getTitles(), HttpStatus.OK);
    }

    @GetMapping(value = "/campus")
    public ResponseEntity<List<String>> getCampus(){
        return new ResponseEntity<>(subjectService.getCampus(), HttpStatus.OK);
    }

    @GetMapping(value = "/types")
    public ResponseEntity<List<String>> getTyes(){
        return new ResponseEntity<>(subjectService.getTypes(), HttpStatus.OK);
    }
}
