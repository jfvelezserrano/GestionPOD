package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.dto.*;
import com.urjc.backend.mapper.ICourseMapper;
import com.urjc.backend.mapper.ISubjectMapper;
import com.urjc.backend.mapper.ITeacherMapper;
import com.urjc.backend.model.*;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pods")
public class CourseRestController {

    interface TeacherBase extends TeacherDTO.Base {
    }

    interface SubjectTeacherDTOBase extends SubjectTeacherDTO.Base{
    }

    private final String mainAdmin = "a.merinom.2017@alumnos.urjc.es";

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    ITeacherMapper teacherMapper;

    @Autowired
    ICourseMapper courseMapper;

    @Autowired
    ISubjectMapper subjectMapper;


    @PostMapping(value = "", consumes = { "multipart/form-data"})
    public ResponseEntity<Void> createPOD(@RequestPart("course") String course,
                                                     @RequestPart("fileSubjects") MultipartFile fileSubjects,
                                                     @RequestPart("fileTeachers") MultipartFile fileTeachers) {

        if(!courseService.exists(course)) {
            Course newCourse = new Course(course);
            courseService.save(newCourse);

            teacherService.deleteAdmins();

            if (!subjectService.saveAll(fileSubjects, newCourse)) {
                deleteCourse(newCourse.getId());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (!teacherService.saveAll(fileTeachers, newCourse)) {
                deleteCourse(newCourse.getId());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "")
    public ResponseEntity<List<CourseDTO>> getCourses(){

        List<Course> courses = courseService.findAllOrderByCreationDate();
        return new ResponseEntity<>(courseMapper.map(courses), HttpStatus.OK);
    }

    @JsonView(SubjectTeacherDTOBase.class)
    @GetMapping("/{id}/subjects")
    public ResponseEntity<List<SubjectTeacherDTO>> getSubjectsByIdCourse(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @PathVariable Long id, @RequestParam(defaultValue = "name") String typeSort) {

        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()) {
            Pageable pageable = PageRequest.of(page, 12, Sort.by(typeSort).ascending());
            List<Object[]> list = subjectService.findByCoursePage(course.get(), pageable);
            List<SubjectTeacherDTO> subjectTeacherDTOList = subjectMapper.listSubjectTeacherDTOs(list);

            return new ResponseEntity<>(subjectTeacherDTOList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @JsonView(TeacherBase.class)
    @GetMapping("/{id}/teachers")
    public ResponseEntity<List<TeacherDTO>> getTeachersByIdCourse(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @PathVariable Long id, @RequestParam(defaultValue = "name") String typeSort) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()) {
            Pageable pageable = PageRequest.of(page, 12, Sort.by(typeSort).ascending());
            List<Teacher> list = teacherService.findAllByCourse(id, pageable);

            List<TeacherDTO> listDTO = teacherMapper.map(list);

            return new ResponseEntity<>(listDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{idPod}/teachers/{idTeacher}")
    public ResponseEntity<Void> deleteTeacherInCourse(@PathVariable Long idPod, @PathVariable Long idTeacher) {

        Optional<Course> course = courseService.findById(idPod);

        if(course.isPresent()){

            Optional<Teacher> teacher = teacherService.findById(idTeacher);

            if(teacher.isPresent()){
                course.get().deleteTeacher(teacher.get());
                if(courseService.save(course.get()) != null){
                    if(!teacher.get().getEmail().equals(mainAdmin) &&
                            courseService.findLastCourse().get() == course.get() && teacher.get().getRoles().contains("ADMIN")){
                        teacher.get().getRoles().remove("ADMIN");
                        teacherService.save(teacher.get());
                    }
                    if(teacher.get().getCourseTeachers().isEmpty() && !teacher.get().getRoles().contains("ADMIN")){
                        teacherService.delete(teacher.get());
                    }
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{idPod}/subjects/{idSubject}")
    public ResponseEntity<Void> deleteSubjectInCourse(@PathVariable Long idPod, @PathVariable Long idSubject) {

        Optional<Course> course = courseService.findById(idPod);

        if(course.isPresent()){

            Optional<Subject> subject = subjectService.findById(idSubject);

            if(subject.isPresent()){
                course.get().deleteSubject(subject.get());
                if(courseService.save(course.get()) != null){
                    if(subject.get().getCourseSubjects().isEmpty()){
                        subjectService.delete(subject.get());
                    }
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()){
            subjectService.deleteSubjectsNotInAnyCourse(course.get());
            teacherService.deleteTeachersNotInAnyCourse(course.get());
            courseService.delete(course.get());

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/teachers")
    public ResponseEntity<Void> addNewTeacherToCourse(@RequestBody TeacherRequestDTO teacherRequest, @PathVariable Long id) {
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

    @PostMapping("/{id}/subjects")
    public ResponseEntity<Void> addNewSubjectToCourse(@RequestBody SubjectDTO subjectDTO, @PathVariable Long id) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()) {
            Subject subject = subjectMapper.toSubject(subjectDTO);

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

    @GetMapping(value = "/exportCSV")
    public ResponseEntity<Resource> exportCSV() {

        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()) {
            Sort sort = Sort.unsorted();
            List<Object[]> subjectsAndTeachersCurrentCourse =
                    subjectService.searchByCourse(course.get(), "", "", "", "", -1L, sort);

            List<String[]> body = courseService.createContentForCSV(subjectsAndTeachersCurrentCourse);

            String nameCSV = "POD_" + courseService.findLastCourse().get().getName();

            InputStreamResource resource = new InputStreamResource(courseService.writePODInCSV(body));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, nameCSV)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                    .contentType(MediaType.parseMediaType("text/csv; UTF-8"))
                    .body(resource);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
