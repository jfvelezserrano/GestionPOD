package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.EmailRequestResponse;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TeacherRestController {

    private static final Logger log = LoggerFactory.getLogger(CourseRestController.class);

    interface TeacherBaseWithRoles extends Teacher.Base, Teacher.Roles {
    }

    @Autowired
    private TeacherService teacherService;


    @JsonView(TeacherBaseWithRoles.class)
    @PutMapping("/teachers")
    public ResponseEntity<?> updateTeacher(@RequestBody Teacher teacher) {

        Teacher teacherIfExists = teacherService.getTeacherIfExists(teacher);
        if(teacherIfExists != null) {
            Teacher teacherDDBB = teacherService.findIfIsInCurrentCourse(teacherIfExists.getEmail());
            if (teacherDDBB != null || (teacherIfExists.getRoles().contains("ADMIN") && teacherIfExists.getRoles() != teacher.getRoles())) {
                teacherIfExists.setRoles(teacher.getRoles());
                teacherService.saveTeacher(teacherIfExists);
                return new ResponseEntity<>(teacherIfExists, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(TeacherBaseWithRoles.class)
    @GetMapping(value = "/teachers")
    public ResponseEntity<List<Teacher>> findAll(@RequestParam(value = "role", required = false) String role){
        if(role == null){
            return new ResponseEntity<>(teacherService.getAllTeachersInCurrentCourse(), HttpStatus.OK);
        }

        List<Teacher> admins = teacherService.getTeachersByRole(role);

        return new ResponseEntity<>(admins, HttpStatus.OK);
    }
}
