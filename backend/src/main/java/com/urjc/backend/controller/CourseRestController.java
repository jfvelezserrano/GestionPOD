package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.*;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CourseRestController {

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
                                                     @RequestPart("fileTeachers") MultipartFile fileTeachers) {

        if(!courseService.exists(course)) {
            Course newCourse = new Course(course);
            courseService.save(newCourse);

            if (!subjectService.saveAll(fileSubjects, newCourse)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (!teacherService.saveAll(fileTeachers, newCourse)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @JsonView(CourseBase.class)
    @GetMapping(value = "/pods")
    public List<Course> getCourses(){
        return courseService.findAllOrderByCreationDate();
    }

    @JsonView(SubjectBase.class)
    @GetMapping("/pods/{id}/subjects")
    public ResponseEntity<?> getSubjectsByIdCourse(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @PathVariable Long id, @RequestParam(defaultValue = "name") String typeSort) {

        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()) {
            Pageable pageable = PageRequest.of(page, 12, Sort.by(typeSort).ascending());
            List<Object[]> list = subjectService.findByCoursePage(course.get(), pageable);

            return new ResponseEntity<>(list, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(TeacherBase.class)
    @GetMapping("/pods/{id}/teachers")
    public ResponseEntity<List<Teacher>> getTeachersByIdCourse(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @PathVariable Long id, @RequestParam(defaultValue = "name") String typeSort) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()) {
            Pageable pageable = PageRequest.of(page, 12, Sort.by(typeSort).ascending());
            List<Teacher> list = teacherService.findAllByCourse(id, pageable);

            return new ResponseEntity<>(list, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/pods/{idPod}/teachers/{idTeacher}")
    public ResponseEntity<?> deleteTeacherInCourse(@PathVariable Long idPod, @PathVariable Long idTeacher) {

        Optional<Course> course = courseService.findById(idPod);

        if(course.isPresent()){

            Optional<Teacher> teacher = teacherService.findById(idTeacher);

            if(teacher.isPresent()){
                course.get().deleteTeacher(teacher);
                if(courseService.save(course.get()) != null){
                    if(teacher.get().getCourseTeachers().isEmpty() && !teacher.get().getRoles().contains("ADMIN")){
                        teacherService.delete(teacher.get());
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/pods/{idPod}/subjects/{idSubject}")
    public ResponseEntity<?> deleteSubjectInCourse(@PathVariable Long idPod, @PathVariable Long idSubject) {

        Optional<Course> course = courseService.findById(idPod);

        if(course.isPresent()){

            Optional<Subject> subject = subjectService.findById(idSubject);

            if(subject.isPresent()){
                course.get().deleteSubject(subject);
                if(courseService.save(course.get()) != null){
                    if(subject.get().getCourseSubjects().isEmpty()){
                        subjectService.delete(subject.get());
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/pods/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()){
            courseService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/pods/{id}/teachers")
    public ResponseEntity<?> addNewTeacherToCourse(@RequestBody TeacherRequest teacherRequest, @PathVariable Long id) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()) {
            Teacher newTeacher = new Teacher(teacherRequest.getName(), teacherRequest.getEmail());
            Teacher alreadyExists = teacherService.getTeacherIfExists(newTeacher);

            if (alreadyExists == null) {
                try {
                    course.get().addTeacher(newTeacher, teacherRequest.getHours());
                    teacherService.save(newTeacher);
                } catch (RuntimeException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else if(!course.get().isTeacherInCourse(alreadyExists)) {
                try {
                    course.get().addTeacher(alreadyExists, teacherRequest.getHours());
                    teacherService.save(alreadyExists);
                } catch (RuntimeException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/pods/{id}/subjects")
    public ResponseEntity<?> addNewSubjectToCourse(@RequestBody Subject subject, @PathVariable Long id) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()) {

            Subject alreadyExists = subjectService.findSubjectIfExists(subject);

            if (alreadyExists == null) {
                try {
                    course.get().addSubject(subject);
                    subjectService.save(subject);
                } catch (RuntimeException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else if(!course.get().isSubjectInCourse(alreadyExists)) {
                try {
                    course.get().addSubject(alreadyExists);
                    subjectService.save(alreadyExists);
                } catch (RuntimeException e) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
