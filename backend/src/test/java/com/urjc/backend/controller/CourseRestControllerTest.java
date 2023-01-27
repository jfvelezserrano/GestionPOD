package com.urjc.backend.controller;

import com.urjc.backend.Data;
import com.urjc.backend.dto.CourseDTO;
import com.urjc.backend.dto.SubjectTeacherDTO;
import com.urjc.backend.dto.TeacherDTO;
import com.urjc.backend.dto.TeacherJoinCourseDTO;
import com.urjc.backend.error.exception.CSVValidationException;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.error.exception.RedirectException;
import com.urjc.backend.mapper.*;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.CourseServiceImpl;
import com.urjc.backend.service.SubjectServiceImpl;
import com.urjc.backend.service.TeacherServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.urjc.backend.error.ErrorMessageConstants.NOT_FOUND_ID_COURSE;
import static com.urjc.backend.error.ErrorMessageConstants.NO_COURSE_YET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseRestControllerTest {

    private static final String emailMainAdmin = "a.merinom.2017@alumnos.urjc.es";
    private static final String nameMainAdmin = "Alicia Merino Martinez";

    @Mock
    SubjectServiceImpl subjectService;

    @Mock
    CourseServiceImpl courseService;

    @Mock
    TeacherServiceImpl teacherService;

    @InjectMocks
    CourseRestController courseRestController;

    ITeacherMapper teacherMapper = new ITeacherMapperImpl();

    ICourseMapper courseMapper = new ICourseMapperImpl();

    ISubjectMapper subjectMapper = new ISubjectMapperImpl();

    @Test
    void Should_ThrowBadRequest_When_FilesAreEmptyWhenCreatingPOD() throws IOException {
        Optional<Course> course = Data.createCourse("2022-2023");

        MockMultipartFile fileSubjects = new MockMultipartFile("fileSubjects", Data.createEmptyInputStream());
        MockMultipartFile fileTeachers = new MockMultipartFile("fileTeachers", Data.createEmptyInputStream());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.createPOD(courseMapper.toCourseDTO(course.get()), fileSubjects, fileTeachers);
        });

        assertAll(() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()),
                () -> assertEquals("Se deben añadir dos ficheros, uno con las asignaturas y otro con los docentes", exception.getMessage()));

        verify(courseService, never()).exists(any());
        verify(courseService, never()).save(any());
        verify(subjectService, never()).saveAll(any(), any());
        verify(teacherService, never()).saveAll(any(), any());
        verify(teacherService, never()).updateAdminsInLastCourse();
        verify(courseService, never()).save(any());
    }

    @Test
    void Should_ThrowUnsupportedMediaType_When_ContentTypeIsNotCSV() throws IOException {
        Optional<Course> course = Data.createCourse("2022-2023");

        MockMultipartFile fileSubjects = new MockMultipartFile("fileSubjects", "fileSubjects", "text/csv", Data.createInputStreamSubject());
        MockMultipartFile fileTeachers = new MockMultipartFile("fileTeachers", "fileTeachers", "image/png", Data.createInputStreamTeacher());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.createPOD(courseMapper.toCourseDTO(course.get()), fileSubjects, fileTeachers);
        });

        assertAll(() -> assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, exception.getStatus()),
                () -> assertEquals("Solo se permiten archivos csv", exception.getMessage()));

        verify(courseService, never()).exists(any());
        verify(courseService, never()).save(any());
        verify(subjectService, never()).saveAll(any(), any());
        verify(teacherService, never()).saveAll(any(), any());
        verify(teacherService, never()).updateAdminsInLastCourse();
        verify(courseService, never()).save(any());
    }

    @Test
    void Should_SaveData_When_CreatePOD() throws IOException {
        ReflectionTestUtils.setField(courseRestController, "courseMapper", courseMapper);
        Optional<Course> course = Data.createCourse("2022-2023");

        MockMultipartFile fileSubjects = new MockMultipartFile("fileSubjects", "fileSubjects", "text/csv", Data.createInputStreamSubject());
        MockMultipartFile fileTeachers = new MockMultipartFile("fileTeachers", "fileTeachers", "text/csv", Data.createInputStreamTeacher());

        when(courseService.exists(anyString())).thenReturn(false);
        when(courseService.save(any())).thenReturn(Data.createCourse("2022-2023").get());
        doNothing().when(subjectService).saveAll(any(), any());
        doNothing().when(teacherService).saveAll(any(), any());
        doNothing().when(teacherService).updateAdminsInLastCourse();
        when(courseService.save(any())).thenReturn(Data.createCourse("2022-2023").get());

        courseRestController.createPOD(courseMapper.toCourseDTO(course.get()), fileSubjects, fileTeachers);

        verify(courseService).exists(any());
        verify(courseService, times(2)).save(any());
        verify(subjectService).saveAll(any(), any());
        verify(teacherService).saveAll(any(), any());
        verify(teacherService).updateAdminsInLastCourse();
    }

    @Test
    void Should_ThrowCourseAlreadyExists_When_CreatePOD() throws IOException {
        ReflectionTestUtils.setField(courseRestController, "courseMapper", courseMapper);
        Optional<Course> course = Data.createCourse("2022-2023");

        MockMultipartFile fileSubjects = new MockMultipartFile("fileSubjects", "fileSubjects", "text/csv", Data.createInputStreamSubject());
        MockMultipartFile fileTeachers = new MockMultipartFile("fileTeachers", "fileTeachers", "text/csv", Data.createInputStreamTeacher());

        when(courseService.exists(anyString())).thenReturn(true);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.createPOD(courseMapper.toCourseDTO(course.get()), fileSubjects, fileTeachers);
        });

        assertAll(() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()),
                () -> assertEquals("El nombre introducido ya existe", exception.getMessage()));


        verify(courseService).exists(any());
        verify(courseService, never()).save(any());
        verify(subjectService, never()).saveAll(any(), any());
        verify(teacherService, never()).saveAll(any(), any());
        verify(teacherService, never()).updateAdminsInLastCourse();
    }

    @Test
    void Should_ThrowValidateException_When_CreatePOD() throws IOException {
        ReflectionTestUtils.setField(courseRestController, "courseMapper", courseMapper);
        Optional<Course> course = Data.createCourse("2022-2023");

        MockMultipartFile fileSubjects = new MockMultipartFile("fileSubjects", "fileSubjects", "text/csv", Data.createInputStreamSubject());
        MockMultipartFile fileTeachers = new MockMultipartFile("fileTeachers", "fileTeachers", "text/csv", Data.createInputStreamTeacher());

        when(courseService.exists(anyString())).thenReturn(false);

        doAnswer(i -> {
            Course arg0 = i.getArgument(0);
            arg0.setId(1l);
            return arg0;
        }).when(courseService).save(any());

        when(courseService.findById(anyLong())).thenReturn(Data.createCourse("2022-2023"));
        doNothing().when(subjectService).deleteSubjectsByCourse(any());
        doNothing().when(teacherService).deleteTeachersByCourse(any());
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        doNothing().when(courseService).delete(any());

        doThrow(new CSVValidationException("Algún dato es incorrecto", null))
                .when(subjectService).saveAll(any(), any());

        CSVValidationException exception = assertThrows(CSVValidationException.class, () -> {
            courseRestController.createPOD(courseMapper.toCourseDTO(course.get()), fileSubjects, fileTeachers);
        });

        assertAll(() -> assertEquals("Algún dato es incorrecto", exception.getMessage()));

        verify(courseService).exists(any());
        verify(courseService).save(any());
        verify(subjectService).saveAll(any(), any());
        verify(teacherService, never()).saveAll(any(), any());
        verify(teacherService, never()).updateAdminsInLastCourse();
    }

    @Test
    void Should_ThrowDefectiveFile_When_CreatePOD() throws IOException {
        ReflectionTestUtils.setField(courseRestController, "courseMapper", courseMapper);
        Optional<Course> course = Data.createCourse("2022-2023");

        MockMultipartFile fileSubjects = new MockMultipartFile("fileSubjects", "fileSubjects", "text/csv", Data.createInputStreamSubject());
        MockMultipartFile fileTeachers = new MockMultipartFile("fileTeachers", "fileTeachers", "text/csv", Data.createInputStreamTeacher());

        when(courseService.exists(anyString())).thenReturn(false);

        doAnswer(i -> {
            Course arg0 = i.getArgument(0);
            arg0.setId(1l);
            return arg0;
        }).when(courseService).save(any());

        when(courseService.findById(anyLong())).thenReturn(Data.createCourse("2022-2023"));
        doNothing().when(subjectService).deleteSubjectsByCourse(any());
        doNothing().when(teacherService).deleteTeachersByCourse(any());
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        doNothing().when(courseService).delete(any());

        doThrow(IOException.class).when(subjectService).saveAll(any(), any());

        Exception exception = assertThrows(Exception.class, () -> {
            courseRestController.createPOD(courseMapper.toCourseDTO(course.get()), fileSubjects, fileTeachers);
        });

        assertAll(() -> assertEquals("Alguno de los ficheros está defectuoso", exception.getMessage()));

        verify(courseService).exists(any());
        verify(courseService).save(any());
        verify(subjectService).saveAll(any(), any());
        verify(teacherService, never()).saveAll(any(), any());
        verify(teacherService, never()).updateAdminsInLastCourse();
    }

    @Test
    void Should_ReturnList_When_GetCourses(){
        ReflectionTestUtils.setField(courseRestController, "courseMapper", courseMapper);

        when(courseService.findAllOrderByCreationDate()).thenReturn(Data.createListCourse("2022-2023", "2021-2022"));

        ResponseEntity<List<CourseDTO>> result = courseRestController.getCourses();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(CourseDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals("2022-2023", result.getBody().get(0).getName()),
                () -> assertEquals("2021-2022", result.getBody().get(1).getName()),
                () -> assertEquals(2, result.getBody().size()));

        verify(courseService).findAllOrderByCreationDate();
    }

    @Test
    void Should_ReturnCourse_When_GetCurrentCourse(){
        ReflectionTestUtils.setField(courseRestController, "courseMapper", courseMapper);

        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));

        ResponseEntity<CourseDTO> result = courseRestController.getCurrentCourse();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(CourseDTO.class, result.getBody().getClass()),
                () -> assertEquals("2022-2023", result.getBody().getName()));

        verify(courseService).findLastCourse();
    }

    @Test
    void Should_ThrowException_When_GetCurrentCourse(){
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.getCurrentCourse();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
    }

    @Test
    void Should_ReturnSubjectsWithTeachers_When_GetSubjectsByIdCourse(){
        ReflectionTestUtils.setField(courseRestController, "subjectMapper", subjectMapper);

        when(courseService.findById(anyLong())).thenReturn(Data.createCourse("2022-2023"));
        when(subjectService.findByCoursePage(any(), any())).thenReturn(DataControllers.createSubjectsWithTeachers());

        ResponseEntity<List<SubjectTeacherDTO>> result = courseRestController.getSubjectsByIdCourse(0, 1l, "name");

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(SubjectTeacherDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals(2, result.getBody().size()),
                () -> assertTrue(result.getBody().stream().anyMatch(s -> s.getSubject().getCode().equals("2323232") &&
                        s.getSubject().getName().equals("Estructuras de datos") &&
                        s.getJoinedTeachers().get(0).equals("30h Luis Rodriguez"))),
                () -> assertTrue(result.getBody().stream().anyMatch(s -> s.getSubject().getCode().equals("4545454") &&
                        s.getSubject().getName().equals("Bases de datos") &&
                        s.getJoinedTeachers().isEmpty())));

        verify(courseService).findById(anyLong());
        verify(subjectService).findByCoursePage(any(), any());
    }

    @Test
    void Should_ThrowException_When_GetSubjectsByIdCourse(){
        when(courseService.findById(anyLong())).thenReturn(Optional.empty());

        RedirectException exception = assertThrows(RedirectException.class, () -> {
            courseRestController.getSubjectsByIdCourse(0, 1l, "name");
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NOT_FOUND_ID_COURSE + 1l, exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(subjectService, never()).findByCoursePage(any(), any());
    }

    @Test
    void Should_ReturnTeachers_When_GetTeachersByIdCourse(){
        ReflectionTestUtils.setField(courseRestController, "teacherMapper", teacherMapper);

        when(courseService.findById(anyLong())).thenReturn(Data.createCourse("2022-2023"));
        when(teacherService.findAllByCourse(any(), any())).thenReturn(Data.createListTeacher());

        ResponseEntity<List<TeacherDTO>> result = courseRestController.getTeachersByIdCourse(0, 1l, "name");

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(TeacherDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals(2, result.getBody().size()),
                () -> assertTrue(result.getBody().stream().anyMatch(t -> t.getName().equals("Pedro López"))),
                () -> assertTrue(result.getBody().stream().anyMatch(t -> t.getName().equals("Luis Rodriguez"))));

        verify(courseService).findById(anyLong());
        verify(teacherService).findAllByCourse(any(), any());
    }

    @Test
    void Should_ThrowException_When_GetTeachersByIdCourse(){
        when(courseService.findById(anyLong())).thenReturn(Optional.empty());

        RedirectException exception = assertThrows(RedirectException.class, () -> {
            courseRestController.getTeachersByIdCourse(0, 1l, "name");
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NOT_FOUND_ID_COURSE + 1l, exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(teacherService, never()).findAllByCourse(any(), any());
    }

    @Test
    void Should_Delete_When_DeleteTeacherInCourse(){
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Teacher> teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com");
        Data.addCourseTeachers(teacher.get(), course.get());
        when(courseService.findById(anyLong())).thenReturn(course);
        when(teacherService.findById(anyLong())).thenReturn(teacher);

        doAnswer(i -> {
            Course arg0 = i.getArgument(0);
            arg0.setId(1l);
            return arg0;
        }).when(courseService).save(any());

        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));

        courseRestController.deleteTeacherInCourse(1l, 1l);

        verify(courseService).findById(anyLong());
        verify(teacherService).findById(anyLong());
        verify(courseService).save(any());
        verify(courseService).findLastCourse();
        verify(teacherService, never()).delete(any());
        verify(teacherService, never()).save(any());
    }

    @Test
    void Should_ThrowBadRequest_When_DeleteMainAdminInCourse(){
        ReflectionTestUtils.setField(courseRestController, "emailMainAdmin", emailMainAdmin);
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Teacher> teacher = Data.createTeacher(nameMainAdmin, emailMainAdmin);
        Data.addCourseTeachers(teacher.get(), course.get());
        when(courseService.findById(anyLong())).thenReturn(course);
        when(teacherService.findById(anyLong())).thenReturn(teacher);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.deleteTeacherInCourse(1l, 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()),
                () -> assertEquals("El administrador principal no se puede borrar del curso", exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(teacherService).findById(anyLong());
        verify(courseService, never()).save(any());
        verify(courseService, never()).findLastCourse();
        verify(teacherService, never()).delete(any());
        verify(teacherService, never()).save(any());
    }

    @Test
    void Should_ThrowException_When_DeletingAndNotExistsCourseOrTeacher(){
        when(courseService.findById(anyLong())).thenReturn(Optional.empty());
        when(teacherService.findById(anyLong())).thenReturn(Optional.empty());

        RedirectException exception = assertThrows(RedirectException.class, () -> {
            courseRestController.deleteTeacherInCourse(1l, 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("El curso y/o el docente no se han encontrado", exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(teacherService).findById(anyLong());
        verify(courseService, never()).save(any());
        verify(courseService, never()).findLastCourse();
        verify(teacherService, never()).delete(any());
        verify(teacherService, never()).save(any());
    }

    @Test
    void Should_Delete_When_DeleteSubjectInCourse(){
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Subject> subject = Data.createSubject("378972727", "Estadística");
        Data.addCourseSubjects(subject.get(), course.get());
        when(courseService.findById(anyLong())).thenReturn(course);
        when(subjectService.findById(anyLong())).thenReturn(subject);

        doAnswer(i -> {
            Course arg0 = i.getArgument(0);
            arg0.setId(1l);
            return arg0;
        }).when(courseService).save(any());

        courseRestController.deleteSubjectInCourse(1l, 1l);

        verify(courseService).findById(anyLong());
        verify(subjectService).findById(anyLong());
        verify(courseService).save(any());
        verify(subjectService, never()).delete(any());
    }

    @Test
    void Should_ThrowException_When_DeletingAndNotExistsCourseOrSubject(){
        when(courseService.findById(anyLong())).thenReturn(Optional.empty());
        when(subjectService.findById(anyLong())).thenReturn(Optional.empty());

        RedirectException exception = assertThrows(RedirectException.class, () -> {
            courseRestController.deleteSubjectInCourse(1l, 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("El curso y/o la asignatura no se han encontrado", exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(subjectService).findById(anyLong());
        verify(courseService, never()).save(any());
        verify(subjectService, never()).delete(any());
    }

    @Test
    void Should_Delete_When_DeleteCourse(){
        Optional<Course> course = Data.createCourse("2022-2023");
        when(courseService.findById(anyLong())).thenReturn(course);
        doNothing().when(subjectService).deleteSubjectsByCourse(any());
        doNothing().when(teacherService).deleteTeachersByCourse(any());
        when(courseService.findLastCourse()).thenReturn(course);
        doNothing().when(courseService).delete(any());
        doNothing().when(teacherService).updateAdminsInLastCourse();

        courseRestController.deleteCourse(1l);

        verify(courseService).findById(anyLong());
        verify(subjectService).deleteSubjectsByCourse(any());
        verify(teacherService).deleteTeachersByCourse(any());
        verify(courseService).findLastCourse();
        verify(courseService).delete(any());
        verify(teacherService).updateAdminsInLastCourse();
    }

    @Test
    void Should_ThrowException_When_DeleteCourse(){
        when(courseService.findById(anyLong())).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.deleteCourse(1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NOT_FOUND_ID_COURSE + 1l, exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(subjectService, never()).deleteSubjectsByCourse(any());
        verify(teacherService, never()).deleteTeachersByCourse(any());
        verify(courseService, never()).findLastCourse();
        verify(courseService, never()).delete(any());
        verify(teacherService, never()).updateAdminsInLastCourse();
    }

    @Test
    void Should_Add_When_AddNewTeacherToCourse(){
        Optional<Course> course = Data.createCourse("2022-2023");
        when(courseService.findById(anyLong())).thenReturn(course);
        when(teacherService.findByEmail(anyString())).thenReturn(Data.createTeacher("Pedro Ramirez", "pedro@pedro.com").get());
        doAnswer(i -> {
            Course arg0 = i.getArgument(0);
            arg0.setId(1l);
            return arg0;
        }).when(courseService).save(any());

        TeacherJoinCourseDTO teacherJoinCourseDTO = new TeacherJoinCourseDTO();
        teacherJoinCourseDTO.setEmail("ejemplo@ejemplo.com");
        teacherJoinCourseDTO.setName("Luis Rodriguez");
        teacherJoinCourseDTO.setHours(120);

        courseRestController.addNewTeacherToCourse(teacherJoinCourseDTO, 1l);

        verify(courseService).findById(anyLong());
        verify(teacherService).findByEmail(anyString());
        verify(teacherService, never()).save(any());
        verify(courseService).save(any());
    }

    @Test
    void Should_ThrowAlreadyExists_When_AddNewTeacherToCourse(){
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Teacher> teacher = Data.createTeacher("Pedro Ramirez", "pedro@pedro.com");
        Data.addCourseTeachers(teacher.get(), course.get());
        when(courseService.findById(anyLong())).thenReturn(course);
        when(teacherService.findByEmail(anyString())).thenReturn(teacher.get());

        TeacherJoinCourseDTO teacherJoinCourseDTO = new TeacherJoinCourseDTO();
        teacherJoinCourseDTO.setEmail("ejemplo@ejemplo.com");
        teacherJoinCourseDTO.setName("Luis Rodriguez");
        teacherJoinCourseDTO.setHours(120);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.addNewTeacherToCourse(teacherJoinCourseDTO, 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.CONFLICT, exception.getStatus()),
                () -> assertEquals("La dirección de correo ya existe", exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(teacherService).findByEmail(anyString());
        verify(teacherService, never()).save(any());
        verify(courseService, never()).save(any());
    }

    @Test
    void Should_ThrowCourseNotFound_When_AddNewTeacherToCourse(){
        when(courseService.findById(anyLong())).thenReturn(Optional.empty());

        TeacherJoinCourseDTO teacherJoinCourseDTO = new TeacherJoinCourseDTO();
        teacherJoinCourseDTO.setEmail("ejemplo@ejemplo.com");
        teacherJoinCourseDTO.setName("Luis Rodriguez");
        teacherJoinCourseDTO.setHours(120);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.addNewTeacherToCourse(teacherJoinCourseDTO, 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NOT_FOUND_ID_COURSE + 1l, exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(teacherService, never()).findByEmail(anyString());
        verify(teacherService, never()).save(any());
        verify(courseService, never()).save(any());
    }

    @Test
    void Should_Add_When_AddNewSubjectToCourse(){
        ReflectionTestUtils.setField(courseRestController, "subjectMapper", subjectMapper);
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Subject> subject = Data.createSubject("54654643", "Estadística");
        when(courseService.findById(anyLong())).thenReturn(course);
        when(subjectService.isCodeInCourse(anyLong(), anyString())).thenReturn(false);
        when(subjectService.findSubjectIfExists(any())).thenReturn(subject.get());

        doAnswer(i -> {
            Course arg0 = i.getArgument(0);
            arg0.setId(1l);
            return arg0;
        }).when(courseService).save(any());

        courseRestController.addNewSubjectToCourse(subjectMapper.toSubjectDTO(subject.get()), 1l);

        verify(courseService).findById(anyLong());
        verify(subjectService).isCodeInCourse(anyLong(), anyString());
        verify(subjectService).findSubjectIfExists(any());
        verify(subjectService, never()).save(any());
        verify(courseService).save(any());
    }

    @Test
    void Should_ThrowAlreadyExists_When_AddNewSubjectToCourse(){
        ReflectionTestUtils.setField(courseRestController, "subjectMapper", subjectMapper);
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Subject> subject = Data.createSubject("54654643", "Estadística");
        when(courseService.findById(anyLong())).thenReturn(course);
        when(subjectService.isCodeInCourse(anyLong(), anyString())).thenReturn(true);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.addNewSubjectToCourse(subjectMapper.toSubjectDTO(subject.get()), 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.CONFLICT, exception.getStatus()),
                () -> assertEquals("Ya existe una asignatura con ese código", exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(subjectService).isCodeInCourse(anyLong(), anyString());
        verify(subjectService, never()).findSubjectIfExists(any());
        verify(subjectService, never()).save(any());
        verify(courseService, never()).save(any());
    }

    @Test
    void Should_ThrowCourseNotFound_When_AddNewSubjectToCourse(){
        Optional<Subject> subject = Data.createSubject("54654643", "Estadística");
        when(courseService.findById(anyLong())).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.addNewSubjectToCourse(subjectMapper.toSubjectDTO(subject.get()), 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NOT_FOUND_ID_COURSE + 1l, exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(subjectService, never()).isCodeInCourse(anyLong(), anyString());
        verify(subjectService, never()).findSubjectIfExists(any());
        verify(subjectService, never()).save(any());
        verify(courseService, never()).save(any());
    }

    @Test
    void Should_CreateBodyCSV_When_ExportCSV() throws IOException {
        Optional<Course> course = Data.createCourse("2022-2023");
        when(courseService.findLastCourse()).thenReturn(course);
        when(subjectService.searchByCourse(any(), anyString(), anyString(),  any(), anyString(), anyString(), any())).thenReturn(Data.createResultSearch());
        when(courseService.createContentForCSV(any())).thenReturn(Data.createBodyForCSV());
        when(courseService.writePODInCSV(any())).thenReturn(DataControllers.createByteArrayInputStream(Data.createBodyForCSV()));

        ResponseEntity<Resource> result = courseRestController.exportCSV();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(MediaType.parseMediaType("text/csv; UTF-8"), result.getHeaders().getContentType()),
                () -> assertEquals("POD_2022-2023", result.getHeaders().getContentDisposition().toString()));

        verify(courseService).findLastCourse();
        verify(subjectService).searchByCourse(any(), anyString(), anyString(),  any(), anyString(), anyString(), any());
        verify(courseService).createContentForCSV(any());
    }

    @Test
    void Should_ThrowExceptionNotPossibleToDownload_When_ExportCSV() throws IOException {
        Optional<Course> course = Data.createCourse("2022-2023");
        when(courseService.findLastCourse()).thenReturn(course);
        when(subjectService.searchByCourse(any(), anyString(), anyString(),  any(), anyString(), anyString(), any())).thenReturn(Data.createResultSearch());
        when(courseService.createContentForCSV(any())).thenReturn(Data.createBodyForCSV());
        when(courseService.writePODInCSV(any())).thenReturn(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.exportCSV();
        });

        assertAll(() -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus()),
                () -> assertEquals("No se ha podido descargar el POD actual", exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(subjectService).searchByCourse(any(), anyString(), anyString(),  any(), anyString(), anyString(), any());
        verify(courseService).createContentForCSV(any());
    }

    @Test
    void Should_ThrowExceptionNoCourseYetWhen_ExportCSV() throws IOException {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            courseRestController.exportCSV();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(subjectService, never()).searchByCourse(any(), anyString(), anyString(),  any(), anyString(), anyString(), any());
        verify(courseService, never()).createContentForCSV(any());
    }
}
