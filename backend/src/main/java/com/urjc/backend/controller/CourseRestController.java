package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.*;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import com.urjc.backend.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CourseRestController {

    private static final Logger log = LoggerFactory.getLogger(CourseRestController.class);

    interface CourseBase extends Course.Base {
    }

    interface TeacherBase extends Teacher.Base {
    }

    interface SubjectBase extends Subject.Base, Schedule.Base {
    }

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private TeacherService teacherService;


    @PostMapping(value = "/pods", consumes = { "multipart/form-data"})
    public @ResponseBody ResponseEntity<?> createPOD(@RequestPart("course") String course,
                                                     @RequestPart("fileSubjects") MultipartFile fileSubjects,
                                                     @RequestPart("fileTeachers") MultipartFile fileTeachers) throws IOException {

        Course newCourse = courseService.createCourse(course);

        if(newCourse == null){
            return new ResponseEntity<>("Ya existe ese curso", HttpStatus.BAD_REQUEST);
        }

        if(!subjectService.saveAllSubjects(fileSubjects, newCourse)){
            return new ResponseEntity<>("El archivo de asignaturas parece no estar bien", HttpStatus.BAD_REQUEST);
        }
        if(!teacherService.saveAllTeachers(fileTeachers, newCourse)){
            return new ResponseEntity<>("El archivo de docentes parece no estar bien", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @JsonView(CourseBase.class)
    @GetMapping(value = "/pods")
    public List<Course> getPODs(){
        return courseService.getCourses();
    }

    @JsonView(SubjectBase.class)
    @GetMapping("/pods/{id}/subjects")
    public ResponseEntity<List<Subject>> getSubjectsByIdPOD(@PathVariable Long id) {
        List<Subject> subjectsInPod = subjectService.getSubjectsByPOD(id);
        if (!subjectsInPod.isEmpty()) {
            return new ResponseEntity<>(subjectsInPod, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @JsonView(TeacherBase.class)
    @GetMapping("/pods/{id}/teachers")
    public ResponseEntity<List<Teacher>> getTeachersByIdPOD(@PathVariable Long id) {

        List<Teacher> teachersInPod = teacherService.getTeachersByPOD(id);
        if (!teachersInPod.isEmpty()) {
            return new ResponseEntity<>(teachersInPod, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/pods/{idPod}/teachers/{idTeacher}")
    public ResponseEntity<?> deleteTeacherInPod(@PathVariable Long idPod, @PathVariable Long idTeacher) {

        Optional<Course> course = courseService.findCourseById(idPod);

        if(course.isPresent()){

            Optional<Teacher> teacher = teacherService.findTeacherById(idTeacher);

            if(teacher.isPresent()){
                course.get().deleteTeacher(teacher);
                if(courseService.updateCourse(course) != null){
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/pods/{idPod}/subjects/{idSubject}")
    public ResponseEntity<?> deleteSubjectInPod(@PathVariable Long idPod, @PathVariable Long idSubject) {

        Optional<Course> course = courseService.findCourseById(idPod);

        if(course.isPresent()){

            Optional<Subject> subject = subjectService.findSubjectById(idSubject);

            if(subject.isPresent()){
                course.get().deleteSubject(subject);
                if(courseService.updateCourse(course) != null){
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
