package com.urjc.backend.service;

import com.urjc.backend.Data;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.urjc.backend.service.DataServices.createBarGraphData;
import static com.urjc.backend.service.DataServices.createDoughnutChartData;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubjectServiceTest {

    @Mock
    SubjectRepository subjectRepository;

    @InjectMocks
    SubjectServiceImpl subjectService;

    @Test
    void Should_ReturnSubject_When_Save() {
        when(subjectRepository.save(any())).then(i ->{
            Subject s = i.getArgument(0);
            s.setId(1L);
            return s;
        });

        Subject result = subjectService.save(Data.createSubject("4524524", "Estructuras de datos").get());
        assertAll(() -> assertEquals("Estructuras de datos", result.getName()),
                () -> assertEquals("Móstoles", result.getCampus()),
                () -> assertEquals(1l, result.getId()));
        verify(subjectRepository).save(any());
    }

    @Test
    void Should_Delete_When_DeleteOneSubject() {
        Optional<Subject> subject = Data.createSubject("4524524", "Estructuras de datos");
        doAnswer(i -> {
            Subject arg0 = i.getArgument(0);
            assertEquals(arg0.getName(), subject.get().getName());
            return null;
        }).when(subjectRepository).delete(any());

        subjectService.delete(subject.get());
        verify(subjectRepository).delete(any());
    }

    @Test
    void Should_ReturnSubject_When_FindById() {
        when(subjectRepository.findById(anyLong())).thenReturn(Data.createSubject("4524524", "Estructuras de datos"));
        Optional<Subject> result = subjectService.findById(anyLong());
        assertAll(() -> assertTrue(result.isPresent()),
                () -> assertEquals("Estructuras de datos", result.get().getName()));
        verify(subjectRepository).findById(anyLong());
    }

    @Test
    void Should_ReturnEmptySubject_When_FindByNonExistentId() {
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.empty());
        Optional<Subject> result = subjectService.findById(anyLong());
        assertAll(() -> assertTrue(result.isEmpty()));
        verify(subjectRepository).findById(anyLong());
    }

    @Test
    void Should_ReturnSubjectsInPage_When_RequestSubjectsByCourseAndPage() {
        Pageable pageable = PageRequest.of(0, 12, Sort.unsorted());
        when(subjectRepository.findByCoursePage(any(), any())).thenReturn(new PageImpl<>(Data.createListSubject()));

        List<Object[]> result = subjectService.findByCoursePage(Data.createCourse("2022-2023").get(), pageable);
        assertAll(() -> assertFalse(result.isEmpty()),
                () -> assertTrue(result.stream().anyMatch(s -> ((Subject) s[0]).getName().equals("Estadística"))),
                () -> assertTrue(result.stream().anyMatch(s -> ((Subject) s[0]).getName().equals("Multimedia"))),
                () -> assertTrue(result.stream().anyMatch(s -> ((List<String>) s[1]).get(0).equals("30h Luis Rodriguez"))));

        verify(subjectRepository).findByCoursePage(any(), any());
    }

    @Test
    void Should_ReturnList_When_SearchSubjectsBySpecificValues() {
        when(subjectRepository.search(anyLong(), anyString(), any(), anyString(), anyString(), any())).thenReturn(Data.createListSubject());
        List<Object[]> result = subjectService.searchByCourse(Data.createCourse("2022-2023").get(),
                "Libre", "Segundo Cuatrimestre", 'M', "(2034) Grado Ingeniería Software (M)",
                "ejemplo@ejemplo.com", Sort.unsorted());

        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(s -> ((Subject) s[0]).getName().equals("Estadística"))),
                () -> assertTrue(result.stream().anyMatch(s -> ((Subject) s[0]).getName().equals("Multimedia"))),
                () -> assertTrue(result.stream().anyMatch(s -> ((Subject) s[0]).getTotalHours() - 30 == ((Integer) s[2]))),
                () -> assertTrue(result.stream().anyMatch(s -> ((Subject) s[0]).getTitle().equals("(2034) Grado Ingeniería Software (M)"))),
                () -> assertTrue(result.stream().anyMatch(s -> ((Integer) s[2]) < ((Subject) s[0]).getTotalHours())));

        verify(subjectRepository).search(anyLong(), anyString(), any(), anyString(), anyString(), any());
    }

    @Test
    void Should_ReturnEmptyList_When_SearchSubjectsByNonCommonValues() {
        when(subjectRepository.search(anyLong(), anyString(), any(), anyString(), anyString(), any())).thenReturn(Collections.emptyList());
        List<Object[]> result = subjectService.searchByCourse(Data.createCourse("2022-2023").get(),
                "Libre", "Segundo Cuatrimestre", 'T', "(2034) Grado Ingeniería Software (M)",
                "ejemplo@ejemplo.com", Sort.unsorted());

        assertAll(() -> assertEquals(0, result.size()));
        verify(subjectRepository).search(anyLong(), anyString(), any(), anyString(), anyString(), any());
    }

    @Test
    void Should_ReturnSubjects_When_FindByCourse() {
        when(subjectRepository.findByCourse(anyLong())).thenReturn(Data.createListSubject());
        List<Subject> result = subjectService.findByCourse(Data.createCourse("2022-2023").get().getId());

        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(s -> s.getCourseSubjects()
                        .stream().anyMatch(cs -> cs.getCourse().getName().equals("2022-2023")))));

        verify(subjectRepository).findByCourse(anyLong());
    }

    @Test
    void Should_ReturnEmptyList_When_FindByNonExistentCourse() {
        when(subjectRepository.findByCourse(anyLong())).thenReturn(Collections.emptyList());
        List<Subject> result = subjectService.findByCourse(Data.createCourse("2023-2024").get().getId());
        assertAll(() -> assertEquals(0, result.size()));
        verify(subjectRepository).findByCourse(anyLong());
    }

    @Test
    void Should_DeleteSubjectFromDDBB_When_DeleteByCourse() {
        Optional<Course> course = Data.createCourse("2022-2023");
        when(subjectRepository.findByCourse(anyLong())).thenReturn(Data.createListSubject());

        doAnswer(i -> {
            Subject arg0 = i.getArgument(0);
            assertEquals("Multimedia", arg0.getName());
            return null;
        }).when(subjectRepository).delete(any());

        doAnswer(i -> {
            Subject arg0 = i.getArgument(0);
            assertEquals("Estadística", arg0.getName());
            return null;
        }).when(subjectRepository).save(any());

        subjectService.deleteSubjectsByCourse(course.get());

        verify(subjectRepository).findByCourse(anyLong());
        verify(subjectRepository).delete(any());
        verify(subjectRepository).save(any());
    }

    @Test
    void Should_DoNothing_When_NotExistSubjectsInCourse() {
        Optional<Course> course = Data.createCourse("2023-2024");
        when(subjectRepository.findByCourse(anyLong())).thenReturn(Collections.emptyList());
        subjectService.deleteSubjectsByCourse(course.get());
        verify(subjectRepository, never()).delete(any());
        verify(subjectRepository, never()).save(any());
    }

    @Test
    void Should_ReturnSubjects_When_FindByCourseAndTeacher() {
        Optional<Course> course = Data.createCourse("2022-2023");
        when(subjectRepository.findByCourseAndTeacher(anyLong(), anyLong(), any())).thenReturn(Data.createListSubject());
        List<Subject> result = subjectService.findByCourseAndTeacher(1l, course.get(), Sort.unsorted());
        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream()
                        .anyMatch(s -> s.getPods().stream().anyMatch(p -> p.getCourse().getName().equals("2022-2023")
                                && p.getTeacher().getId() == 1l))));
        verify(subjectRepository).findByCourseAndTeacher(anyLong(), anyLong(), any());
    }

    @Test
    void Should_ReturnBarGraphData_When_RequestHoursPerSubject() {
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Teacher> teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com");
        when(subjectRepository.hoursPerSubjectByTeacherAndCourse(anyLong(), anyLong(), any())).thenReturn(createBarGraphData());
        List<Object[]> result = subjectService.hoursPerSubjectByTeacherAndCourse(teacher.get(), course.get(), Sort.unsorted());
        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(i -> i[0].equals("Multimedia") && ((int) i[1]) == 100 && ((int) i[2]) == 50)),
                () -> assertTrue(result.stream().anyMatch(i -> i[0].equals("Estadística") && ((int) i[1]) == 120 && ((int) i[2]) == 40)));
        verify(subjectRepository).hoursPerSubjectByTeacherAndCourse(anyLong(), anyLong(), any());
    }

    @Test
    void Should_ReturnDoughnutChartData_When_RequestPercentageHours() {
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Teacher> teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com");
        when(subjectRepository.percentageHoursByTeacherAndCourse(anyLong(), anyLong(), anyInt(), any())).thenReturn(createDoughnutChartData());
        when(subjectRepository.totalChosenHoursByTeacherAndCourse(anyLong(), anyLong())).thenReturn(90);
        List<Object[]> result = subjectService.percentageHoursByTeacherAndCourse(teacher.get(), course.get(), Sort.unsorted());
        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(i -> i[0].equals("Multimedia") && ((int) i[1]) == 40)),
                () -> assertTrue(result.stream().anyMatch(i -> i[0].equals("Estadística") && ((int) i[1]) == 60)),
                () -> assertEquals(100, ((int) result.get(0)[1]) + ((int) result.get(1)[1])));
        verify(subjectRepository).percentageHoursByTeacherAndCourse(anyLong(), anyLong(), anyInt(), any());
        verify(subjectRepository).totalChosenHoursByTeacherAndCourse(anyLong(), anyLong());
    }

    @Test
    void Should_ReturnConflicts_When_FindConflictsByTeacherAndCourse() {
        Optional<Course> course = Data.createCourse("2022-2023");
        when(subjectRepository.findByCourseAndTeacher(anyLong(), anyLong(), any())).thenReturn(Data.createListSubject());
        List<Object[]> result = subjectService.findConflictsByTeacherAndCourse(1l, course.get(), Sort.unsorted());
        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(i -> ((Subject) i[0]).getCode().equals("789456")
                        && ((ArrayList) i[3]).get(0).equals("Multimedia - Solapamiento de horarios"))),
                () -> assertTrue(result.stream().anyMatch(i -> ((Subject) i[0]).getCode().equals("1223456")
                        && ((ArrayList) i[3]).get(0).equals("Estadística - Solapamiento de horarios"))));
        verify(subjectRepository).findByCourseAndTeacher(anyLong(), anyLong(), any());
    }

    @Test
    void Should_ReturnEmptyList_When_FindConflictsByTeacherAndCourse() {
        Optional<Course> course = Data.createCourse("2022-2023");
        when(subjectRepository.findByCourseAndTeacher(anyLong(), anyLong(), any())).thenReturn(Collections.emptyList());
        List<Object[]> result = subjectService.findConflictsByTeacherAndCourse(1l, course.get(), Sort.unsorted());
        assertAll(() -> assertTrue(result.isEmpty()));
        verify(subjectRepository).findByCourseAndTeacher(anyLong(), anyLong(), any());
    }

    @Test
    void Should_SaveSubjectsInDDBB_When_SaveFileSubjects() throws IOException {
        when(subjectRepository.findByCourseAndCode(anyLong(), anyString())).thenReturn(null);
        when(subjectRepository.findSameValues(any())).thenReturn(Collections.emptyList());
        when(subjectRepository.save(any())).then(i ->{
            Subject s = i.getArgument(0);
            assertAll(() -> assertTrue(s.getCode().equals("2241019G1") || s.getCode().equals("2061012")));
            return s;
        });

        Optional<Course> course = Data.createCourse("2022-2023");
        subjectService.saveAll(Data.createInputStreamSubject(), course.get());

        verify(subjectRepository, times(2)).findByCourseAndCode(anyLong(), anyString());
        verify(subjectRepository, times(2)).findSameValues(any());
        verify(subjectRepository, times(2)).save(any());
    }

    @Test
    void Should_ThrowException_When_SaveWrongFileSubjects() {
        Optional<Course> course = Data.createCourse("2022-2023");

        assertThrows(GlobalException.class, () -> {
            subjectService.saveAll(Data.createInputStreamSubjectError(), course.get());
        });

        verify(subjectRepository, never()).findByCourseAndCode(anyLong(), anyString());
        verify(subjectRepository, never()).findSameValues(any());
        verify(subjectRepository, never()).save(any());
    }

    @Test
    void Should_NotSaveSubjects_When_SubjectsCodeAlreadyExistsInCourse() throws IOException {
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Subject> subject = Data.createSubject("2061012", "Programación Orientada a Objetos");
        when(subjectRepository.findByCourseAndCode(anyLong(), anyString()))
                .thenReturn(subject.get());

        subjectService.saveAll(Data.createInputStreamSubject(), course.get());

        verify(subjectRepository, atLeastOnce()).findByCourseAndCode(anyLong(), anyString());
        verify(subjectRepository, never()).findSameValues(any());
        verify(subjectRepository, never()).save(any());
    }

    @Test
    void Should_DoNothing_When_FileSubjectsIsEmpty() throws IOException {
        Optional<Course> course = Data.createCourse("2022-2023");

        subjectService.saveAll(Data.createEmptyInputStream(), course.get());

        verify(subjectRepository, never()).findByCourseAndCode(anyLong(), anyString());
        verify(subjectRepository, never()).findSameValues(any());
        verify(subjectRepository, never()).save(any());
    }

    @Test
    void Should_ReturnFalse_When_NotIsCodeInCourse() {
        when(subjectRepository.findByCourseAndCode(anyLong(), anyString())).thenReturn(null);
        boolean result = subjectService.isCodeInCourse(1l, "123456");
        assertAll(() -> assertFalse(result));
        verify(subjectRepository).findByCourseAndCode(anyLong(), anyString());
    }

    @Test
    void Should_ReturnTrue_When_IsCodeInCourse() {
        Optional<Subject> subject = Data.createSubject("2061012", "Programación Orientada a Objetos");
        when(subjectRepository.findByCourseAndCode(anyLong(), anyString())).thenReturn(subject.get());
        boolean result = subjectService.isCodeInCourse(1l, "2061012");
        assertAll(() -> assertTrue(result));
        verify(subjectRepository).findByCourseAndCode(anyLong(), anyString());
    }

    @Test
    void Should_ReturnSubject_When_FindSubjectIfExists() {
        Optional<Subject> subject = Data.createSubject("1223456", "Multimedia");
        when(subjectRepository.findSameValues(any())).thenReturn(Data.createListSubject());
        Subject result = subjectService.findSubjectIfExists(subject.get());
        assertAll(() -> assertNotEquals(null, result),
                () -> assertEquals("1223456", result.getCode()),
                () -> assertEquals("Multimedia", result.getName()));

        verify(subjectRepository).findSameValues(any());
    }

    @Test
    void Should_ReturnNull_When_NotExistsSubjectsWithSameValues() {
        when(subjectRepository.findSameValues(any())).thenReturn(Collections.emptyList());
        Subject result = subjectService.findSubjectIfExists(any());
        assertAll(() -> assertEquals(null, result));
        verify(subjectRepository).findSameValues(any());
    }

    @Test
    void Should_ReturnTitles_When_RequestGetTitles() {
        when(subjectRepository.getTitles()).thenReturn(Data.createListStrings("(2034) Grado Ingeniería Software (M)", "(2033) Grado Ingeniería Informática (M)"));
        List<String> result = subjectService.getTitles();
        assertAll(() -> assertEquals(2, result.size()),
                () -> result.stream().anyMatch(s -> s.equals("(2034) Grado Ingeniería Software (M)")),
                () -> result.stream().anyMatch(s -> s.equals("(2033) Grado Ingeniería Informática (M)")));
        verify(subjectRepository).getTitles();
    }

    @Test
    void Should_ReturnTitles_When_RequestGetTitlesByCourse() {
        Optional<Course> course = Data.createCourse("2022-2023");
        when(subjectRepository.getTitlesByCourse(anyLong())).thenReturn(Data.createListStrings("(2034) Grado Ingeniería Software (M)", "(2033) Grado Ingeniería Informática (M)"));
        List<String> result = subjectService.getTitlesByCourse(course.get().getId());
        assertAll(() -> assertEquals(2, result.size()),
                () -> result.stream().anyMatch(s -> s.equals("(2034) Grado Ingeniería Software (M)")),
                () -> result.stream().anyMatch(s -> s.equals("(2033) Grado Ingeniería Informática (M)")));
        verify(subjectRepository).getTitlesByCourse(anyLong());
    }

    @Test
    void Should_ReturnCampus_When_RequestGetCampus() {
        when(subjectRepository.getCampus()).thenReturn(Data.createListStrings("Fuenlabrada", "Móstoles", "Alcorcón"));
        List<String> result = subjectService.getCampus();
        assertAll(() -> assertEquals(3, result.size()),
                () -> result.stream().anyMatch(s -> s.equals("Fuenlabrada")),
                () -> result.stream().anyMatch(s -> s.equals("Móstoles")),
                () -> result.stream().anyMatch(s -> s.equals("Alcorcón")));
        verify(subjectRepository).getCampus();
    }

    @Test
    void Should_ReturnTypes_When_RequestGetTypes() {
        when(subjectRepository.getTypes()).thenReturn(Data.createListStrings("Obligatoria", "Optativa", "Formación Básica"));
        List<String> result = subjectService.getTypes();
        assertAll(() -> assertEquals(3, result.size()),
                () -> result.stream().anyMatch(s -> s.equals("Obligatoria")),
                () -> result.stream().anyMatch(s -> s.equals("Optativa")),
                () -> result.stream().anyMatch(s -> s.equals("Formación Básica")));
        verify(subjectRepository).getTypes();
    }
}
