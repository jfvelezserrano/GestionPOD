package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherRestController {

    interface TeacherBaseWithRoles extends Teacher.Base, Teacher.Roles {
    }

    @Autowired
    private TeacherService teacherService;


    @JsonView(TeacherBaseWithRoles.class)
    @PutMapping("")
    public ResponseEntity<?> update(@RequestBody Teacher teacher) {

        Teacher teacherIfExists = teacherService.getTeacherIfExists(teacher);
        if(teacherIfExists != null) {
            Teacher teacherDDBB = teacherService.findIfIsInCurrentCourse(teacherIfExists.getEmail());
            if (teacherDDBB != null || (teacherIfExists.getRoles().contains("ADMIN") && teacherIfExists.getRoles() != teacher.getRoles())) {
                teacherIfExists.setRoles(teacher.getRoles());
                teacherService.save(teacherIfExists);
                return new ResponseEntity<>(teacherIfExists, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(TeacherBaseWithRoles.class)
    @GetMapping(value = "")
    public ResponseEntity<List<Teacher>> findAll(@RequestParam(value = "role", required = false) String role){
        if(role == null){
            return new ResponseEntity<>(teacherService.findAllInCurrentCourse(), HttpStatus.OK);
        }

        List<Teacher> admins = teacherService.findAllByRole(role);

        return new ResponseEntity<>(admins, HttpStatus.OK);
    }
}
