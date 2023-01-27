package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.dto.*;
import com.urjc.backend.error.exception.*;
import com.urjc.backend.mapper.ICourseMapper;
import com.urjc.backend.mapper.ISubjectMapper;
import com.urjc.backend.mapper.ITeacherMapper;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

import static com.urjc.backend.error.ErrorMessageConstants.NOT_FOUND_ID_COURSE;
import static com.urjc.backend.error.ErrorMessageConstants.NO_COURSE_YET;

@Validated
@RestController
@RequestMapping("/api/pods")
public class CourseRestController {

    interface TeacherBase extends TeacherDTO.Base {
    }

    interface SubjectTeacherDTOBase extends SubjectTeacherDTO.Base{
    }

    public static final String TYPE_FILE = "text/csv";

    @Value("${email.main.admin}")
    private String emailMainAdmin;

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
    public ResponseEntity<Void> createPOD(@RequestPart("course") @Valid CourseDTO courseDTO,
                                                     @RequestPart("fileSubjects") MultipartFile fileSubjects,
                                                     @RequestPart("fileTeachers") MultipartFile fileTeachers){


        if(fileSubjects.isEmpty() || fileTeachers.isEmpty()){
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Se deben añadir dos ficheros, uno con las asignaturas y otro con los docentes");
        }
        else if (!TYPE_FILE.equals(fileSubjects.getContentType()) || !TYPE_FILE.equals(fileTeachers.getContentType())) {
            throw new GlobalException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Solo se permiten archivos csv");
        }

        String course = courseMapper.toCourse(courseDTO).getName();

        if(!courseService.exists(course)) {
            Course newCourse = new Course(course);

            try{
                courseService.save(newCourse);
                subjectService.saveAll(fileSubjects.getInputStream(), newCourse);
                teacherService.saveAll(fileTeachers.getInputStream(), newCourse);
                teacherService.updateAdminsInLastCourse();
                courseService.save(newCourse);
            }
            catch (CSVValidationException e) {
                deleteCourse(newCourse.getId());
                throw new CSVValidationException(e.getMessage(), e.getViolations());
            }
            catch (GlobalException e) {
                deleteCourse(newCourse.getId());
                throw new GlobalException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
            catch (Exception e) {
                deleteCourse(newCourse.getId());
                throw new GlobalException(HttpStatus.BAD_REQUEST, "Alguno de los ficheros está defectuoso");
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            throw new GlobalException(HttpStatus.BAD_REQUEST, "El nombre introducido ya existe");
        }
    }

    @GetMapping(value = "")
    public ResponseEntity<List<CourseDTO>> getCourses(){
        List<Course> courses = courseService.findAllOrderByCreationDate();
        return new ResponseEntity<>(courseMapper.map(courses), HttpStatus.OK);
    }

    @GetMapping(value = "/currentCourse")
    public ResponseEntity<CourseDTO> getCurrentCourse(){
        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()) {
            return new ResponseEntity<>(courseMapper.toCourseDTO(course.get()), HttpStatus.OK);
        } else{
            throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
        }
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
        throw new RedirectException(HttpStatus.NOT_FOUND, NOT_FOUND_ID_COURSE + id);
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
        throw new RedirectException(HttpStatus.NOT_FOUND, NOT_FOUND_ID_COURSE + id);
    }

    @DeleteMapping("/{idPod}/teachers/{idTeacher}")
    public ResponseEntity<Void> deleteTeacherInCourse(@PathVariable Long idPod, @PathVariable Long idTeacher) {
        Optional<Course> course = courseService.findById(idPod);
        Optional<Teacher> teacher = teacherService.findById(idTeacher);

        if(course.isPresent() && teacher.isPresent() && course.get().isTeacherInCourse(teacher.get())) {
            if (teacher.get().getEmail().equals(emailMainAdmin)) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "El administrador principal no se puede borrar del curso");
            }

            course.get().deleteTeacher(teacher.get());
            courseService.save(course.get());
            Optional<Course> lastCourse = courseService.findLastCourse();

            if (teacher.get().getCourseTeachers().isEmpty()) {
                teacherService.delete(teacher.get());
            }else if (lastCourse.isPresent() && lastCourse.get() == course.get() && teacher.get().getRoles().contains("ADMIN")) {
                teacher.get().getRoles().remove("ADMIN");
                teacherService.save(teacher.get());
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new RedirectException(HttpStatus.NOT_FOUND, "El curso y/o el docente no se han encontrado");
    }

    @DeleteMapping("/{idPod}/subjects/{idSubject}")
    public ResponseEntity<Void> deleteSubjectInCourse(@PathVariable Long idPod, @PathVariable Long idSubject) {
        Optional<Course> course = courseService.findById(idPod);
        Optional<Subject> subject = subjectService.findById(idSubject);

        if(course.isPresent() && subject.isPresent() && course.get().isSubjectInCourse(subject.get())){
            course.get().deleteSubject(subject.get());
            courseService.save(course.get());
            if(subject.get().getCourseSubjects().isEmpty()){
                subjectService.delete(subject.get());
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new RedirectException(HttpStatus.NOT_FOUND, "El curso y/o la asignatura no se han encontrado");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()){
            subjectService.deleteSubjectsByCourse(course.get());
            teacherService.deleteTeachersByCourse(course.get());
            Optional<Course> lastCourse = courseService.findLastCourse();
            boolean isLastCourse = lastCourse.isPresent() && course.get() == lastCourse.get();
            courseService.delete(course.get());

            if(isLastCourse){
                teacherService.updateAdminsInLastCourse();
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NOT_FOUND_ID_COURSE + id);
    }

    @PostMapping("/{id}/teachers")
    public ResponseEntity<Void> addNewTeacherToCourse(@RequestBody @Valid TeacherJoinCourseDTO teacherRequest, @PathVariable Long id) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()) {
            Teacher teacherDDBB = teacherService.findByEmail(teacherRequest.getEmail());
            if(teacherDDBB == null){
                Teacher newTeacher = new Teacher(teacherRequest.getName(), teacherRequest.getEmail());
                teacherDDBB = teacherService.save(newTeacher);
            }

            if(!course.get().isTeacherInCourse(teacherDDBB)){
                course.get().addTeacher(teacherDDBB, teacherRequest.getHours());
                courseService.save(course.get());
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            throw new GlobalException(HttpStatus.CONFLICT, "La dirección de correo ya existe");
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NOT_FOUND_ID_COURSE + id);
    }

    @PostMapping("/{id}/subjects")
    public ResponseEntity<Void> addNewSubjectToCourse(@RequestBody @Valid SubjectDTO subjectDTO, @PathVariable Long id) {
        Optional<Course> course = courseService.findById(id);
        if(course.isPresent()) {
            Subject subject = subjectMapper.toSubject(subjectDTO);

            if(!subjectService.isCodeInCourse(id,subject.getCode())) {
                Subject subjectDDBB = subjectService.findSubjectIfExists(subject);

                if(subjectDDBB == null){
                    subjectDDBB = subjectService.save(subject);
                }

                course.get().addSubject(subjectDDBB);
                courseService.save(course.get());
                return new ResponseEntity<>(HttpStatus.CREATED);

            }
            throw new GlobalException(HttpStatus.CONFLICT, "Ya existe una asignatura con ese código");
        }
        throw new GlobalException(HttpStatus.NOT_FOUND, NOT_FOUND_ID_COURSE + id);
    }

    @GetMapping(value = "/exportCSV")
    public ResponseEntity<Resource> exportCSV() {

        Optional<Course> course = courseService.findLastCourse();
        if(course.isPresent()) {
            Sort sort = Sort.unsorted();
            List<Object[]> subjectsAndTeachersCurrentCourse =
                    subjectService.searchByCourse(course.get(), "", "", null, "", "", sort);

            List<String[]> body = courseService.createContentForCSV(subjectsAndTeachersCurrentCourse);

            String nameCSV = "POD_" + course.get().getName();

            InputStreamResource resource;

            try {
                 resource = new InputStreamResource(courseService.writePODInCSV(body));
            }catch (Exception e){
                throw new GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, "No se ha podido descargar el POD actual");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, nameCSV)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                    .contentType(MediaType.parseMediaType("text/csv; UTF-8"))
                    .body(resource);
        }

        throw new GlobalException(HttpStatus.NOT_FOUND, NO_COURSE_YET);
    }
}
