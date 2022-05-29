package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TeacherRestController {

    private static final Logger log = LoggerFactory.getLogger(CourseRestController.class);

    @Autowired
    private TeacherService teacherService;
}
