package com.urjc.backend.controller;

import com.urjc.backend.Data;
import com.urjc.backend.dto.SubjectDTO;
import com.urjc.backend.dto.SubjectTeacherDTO;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.error.exception.RedirectException;
import com.urjc.backend.mapper.ISubjectMapper;
import com.urjc.backend.mapper.ISubjectMapperImpl;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.service.CourseServiceImpl;
import com.urjc.backend.service.SubjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static com.urjc.backend.error.ErrorMessageConstants.NO_COURSE_YET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectRestControllerTest {

    @Mock
    SubjectServiceImpl subjectService;

    @Mock
    CourseServiceImpl courseService;

    @InjectMocks
    SubjectRestController subjectRestController;

    ISubjectMapper subjectMapper = new ISubjectMapperImpl();


    @Test
    void Should_ReturnTitles_When_RequestGetTitles() {
        when(subjectService.getTitles()).thenReturn(Data.createListStrings("(2034) Grado Ingeniería Software (M)", "(2033) Grado Ingeniería Informática (M)"));
        ResponseEntity<List<String>> result = subjectRestController.getTitles();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(2, result.getBody().size()),
                () -> assertEquals("(2034) Grado Ingeniería Software (M)", result.getBody().get(0)),
                () -> assertEquals("(2033) Grado Ingeniería Informática (M)", result.getBody().get(1)));

        verify(subjectService).getTitles();
    }

    @Test
    void Should_ReturnTitlesInLastCourse_When_RequestGetCurrentTitles() {
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(subjectService.getTitlesByCourse(anyLong())).thenReturn(Data.createListStrings("(2034) Grado Ingeniería Software (M)", "(2033) Grado Ingeniería Informática (M)"));
        ResponseEntity<List<String>> result = subjectRestController.getTitlesCurrentCourse();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(2, result.getBody().size()),
                () -> assertEquals("(2034) Grado Ingeniería Software (M)", result.getBody().get(0)),
                () -> assertEquals("(2033) Grado Ingeniería Informática (M)", result.getBody().get(1)));

        verify(courseService).findLastCourse();
        verify(subjectService).getTitlesByCourse(anyLong());
    }

    @Test
    void Should_ThrowGlobalException_When_RequestGetCurrentTitles() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            subjectRestController.getTitlesCurrentCourse();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(subjectService, never()).getTitlesByCourse(anyLong());
    }

    @Test
    void Should_ReturnSubjectsNameInLastCourse_When_RequestGetCurrentSubjects() {
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(subjectService.getSubjectsByCourse(anyLong())).thenReturn(Data.createListStrings("Estadística", "Multimedia"));
        ResponseEntity<List<String>> result = subjectRestController.getSubjectsCurrentCourse();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(2, result.getBody().size()),
                () -> assertEquals("Estadística", result.getBody().get(0)),
                () -> assertEquals("Multimedia", result.getBody().get(1)));

        verify(courseService).findLastCourse();
        verify(subjectService).getSubjectsByCourse(anyLong());
    }

    @Test
    void Should_ThrowGlobalException_When_RequestGetCurrentSubjects() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            subjectRestController.getSubjectsCurrentCourse();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(subjectService, never()).getSubjectsByCourse(anyLong());
    }

    @Test
    void Should_ReturnCampus_When_RequestGetCampus() {
        when(subjectService.getCampus()).thenReturn(Data.createListStrings("Móstoles", "Fuenlabrada", "Alcorcón"));
        ResponseEntity<List<String>> result = subjectRestController.getCampus();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(3, result.getBody().size()),
                () -> assertEquals("Móstoles", result.getBody().get(0)),
                () -> assertEquals("Fuenlabrada", result.getBody().get(1)),
                () -> assertEquals("Alcorcón", result.getBody().get(2)));

        verify(subjectService).getCampus();
    }

    @Test
    void Should_ReturnTypes_When_RequestGetTypes() {
        when(subjectService.getTypes()).thenReturn(Data.createListStrings("Obligatoria", "Formación Básica"));
        ResponseEntity<List<String>> result = subjectRestController.getTypes();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(2, result.getBody().size()),
                () -> assertEquals("Obligatoria", result.getBody().get(0)),
                () -> assertEquals("Formación Básica", result.getBody().get(1)));

        verify(subjectService).getTypes();
    }

    @Test
    void Should_ReturnSubjectTeacherDTO_When_RequestGetByIdInCurrentCourse() {
        ReflectionTestUtils.setField(subjectRestController, "subjectMapper", subjectMapper);
        Optional<Subject> subject = Data.createSubject("205485654", "Estadística");
        Optional<Course> course = Data.createCourse("2022-2023");
        course.get().addSubject(subject.get());
        when(courseService.findLastCourse()).thenReturn(course);
        when(subjectService.findById(anyLong())).thenReturn(subject);
        ResponseEntity<SubjectTeacherDTO> result = subjectRestController.getByIdInCurrentCourse(1l);

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(SubjectTeacherDTO.class, result.getBody().getClass()),
                () -> assertEquals("Estadística", result.getBody().getSubject().getName()),
                () -> assertEquals(Data.createListStrings("30h Luis Rodriguez"), result.getBody().getJoinedTeachers()));

        verify(courseService).findLastCourse();
        verify(subjectService).findById(anyLong());
    }

    @Test
    void Should_ThrowGlobalException_When_NotExistsLastCourse() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            subjectRestController.getByIdInCurrentCourse(1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(subjectService, never()).findById(anyLong());
    }

    @Test
    void Should_ThrowGlobalException_When_NotExistsSubject() {
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(subjectService.findById(1l)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            subjectRestController.getByIdInCurrentCourse(1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("No existe esa asignatura", exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(subjectService).findById(anyLong());
    }

    @Test
    void Should_ReturnSubjects_When_FindAllInCurrentCourse() {
        ReflectionTestUtils.setField(subjectRestController, "subjectMapper", subjectMapper);
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(subjectService.findByCourse(anyLong())).thenReturn(Data.createListSubject());

        ResponseEntity<List<SubjectDTO>> result = subjectRestController.findAllInCurrentCourse();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(SubjectDTO.class, result.getBody().get(0).getClass()),
                () -> assertTrue(result.getBody().stream().anyMatch(s -> s.getName().equals("Estadística"))),
                () -> assertTrue(result.getBody().stream().anyMatch(s -> s.getName().equals("Multimedia"))));

        verify(courseService).findLastCourse();
        verify(subjectService).findByCourse(anyLong());
    }

    @Test
    void Should_ThrowException_When_FindAllAndNotExistsLastCourse() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            subjectRestController.findAllInCurrentCourse();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(subjectService, never()).findByCourse(anyLong());
    }

    @Test
    void Should_ReturnRecord_When_RequestRecordSubject() {
        ReflectionTestUtils.setField(subjectRestController, "subjectMapper", subjectMapper);

        when(subjectService.findById(anyLong())).thenReturn(Data.createSubject("1235658", "Estadística"));

        ResponseEntity<Map<String, List<String>>> result = subjectRestController.recordSubject(1l);

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(1, result.getBody().size()),
                () -> assertEquals("30h Luis Rodriguez", result.getBody().get("2022-2023").get(0)));

        verify(subjectService).findById(anyLong());
    }

    @Test
    void Should_ThrowException_When_RequestRecordSubjectAndNotExists() {
        when(subjectService.findById(anyLong())).thenReturn(Optional.empty());

        RedirectException exception = assertThrows(RedirectException.class, () -> {
            subjectRestController.recordSubject(1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("No se ha encontrado la asignatura con id 1", exception.getMessage()));

        verify(subjectService).findById(anyLong());
    }

    @Test
    void Should_ReturnSubjectsTeachers_When_Search() {
        ReflectionTestUtils.setField(subjectRestController, "subjectMapper", subjectMapper);

        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        List<Object[]> search = Data.createResultSearch();

        when(subjectService.searchByCourse(any(), anyString(), anyString(), anyChar(), anyString(), anyString(), anyString(), any())).thenReturn(search);

        ResponseEntity<List<SubjectTeacherDTO>> result = subjectRestController.search("",
                "Segundo Cuatrimestre", 'M', "", "", "", "name");

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(SubjectTeacherDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals(2, result.getBody().size()),
                () -> assertTrue(result.getBody().stream().allMatch(s -> s.getSubject().getTurn().equals('M'))),
                () -> assertTrue(result.getBody().stream().allMatch(s -> s.getSubject().getQuarter().equals("Segundo Cuatrimestre"))));

        verify(courseService).findLastCourse();
        verify(subjectService).searchByCourse(any(), anyString(), anyString(), anyChar(), anyString(), anyString(), anyString(), any());
    }

    @Test
    void Should_ThrowException_When_SearchAndNotExistsLastCourse() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            subjectRestController.search("", "Segundo Cuatrimestre", 'M', "", "", "", "name");
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(subjectService, never()).searchByCourse(any(), anyString(),anyString(), anyChar(), anyString(), anyString(), anyString(), any());
    }
}
