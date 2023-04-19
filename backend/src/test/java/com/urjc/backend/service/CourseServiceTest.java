package com.urjc.backend.service;

import com.urjc.backend.Data;
import com.urjc.backend.model.Course;
import com.urjc.backend.repository.CourseRepository;
import com.urjc.backend.repository.SubjectRepository;
import com.urjc.backend.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {
    @Mock
    CourseRepository courseRepository;

    @Mock
    TeacherRepository teacherRepository;

    @Mock
    SubjectRepository subjectRepository;

    @Mock
    SubjectServiceImpl subjectService;

    @InjectMocks
    CourseServiceImpl courseService;

    @Test
    void Should_ReturnTrue_When_FindByName() {
        when(courseRepository.findByName("2022-2023")).thenReturn(Data.createCourse("2022-2023"));
        boolean result = courseService.exists("2022-2023");
        assertAll(() -> assertTrue(result));
        verify(courseRepository).findByName("2022-2023");
    }

    @Test
    void Should_ReturnFalse_When_FindByName() {
        when(courseRepository.findByName("2022-2023")).thenReturn(Optional.empty());
        boolean result = courseService.exists("2022-2023");
        assertAll(() -> assertFalse(result));
        verify(courseRepository).findByName("2022-2023");
    }

    @Test
    void Should_OrderAll_When_OrderByCreationDate() {
        when(courseRepository.OrderByCreationDateDesc()).thenReturn(Data.createListCourse("2021-2022", "2022-2023"));
        List<Course> result = courseService.findAllOrderByCreationDate();
        assertAll(() -> assertFalse(result.isEmpty()),
                () -> assertEquals("2021-2022", result.get(0).getName()),
                () -> assertEquals("2022-2023", result.get(1).getName()),
                () -> assertEquals(2, result.size()));
        verify(courseRepository).OrderByCreationDateDesc();
    }

    @Test
    void Should_ReturnEmptyList_When_OrderByCreationDate() {
        when(courseRepository.OrderByCreationDateDesc()).thenReturn(new ArrayList<>());
        List<Course> result = courseService.findAllOrderByCreationDate();
        assertAll(() -> assertTrue(result.isEmpty()));
        verify(courseRepository).OrderByCreationDateDesc();
    }

    @Test
    void Should_ReturnCourse_When_FindLastCourse() {
        when(courseRepository.findFirst1ByOrderByCreationDateDesc()).thenReturn(Data.createCourse("2022-2023"));
        Optional<Course> result = courseService.findLastCourse();
        assertAll(() -> assertTrue(result.isPresent()),
                () -> assertEquals("2022-2023", result.get().getName()));
        verify(courseRepository).findFirst1ByOrderByCreationDateDesc();
    }

    @Test
    void Should_ReturnEmptyCourse_When_FindLastCourse() {
        when(courseRepository.findFirst1ByOrderByCreationDateDesc()).thenReturn(Optional.empty());
        Optional<Course> result = courseService.findLastCourse();
        assertAll(() -> assertTrue(result.isEmpty()));
        verify(courseRepository).findFirst1ByOrderByCreationDateDesc();
    }

    @Test
    void Should_ReturnCourse_When_FindById() {
        when(courseRepository.findById(anyLong())).thenReturn(Data.createCourse("2022-2023"));
        Optional<Course> result = courseService.findById(1l);
        assertAll(() -> assertTrue(result.isPresent()),
                () -> assertEquals(1l, result.get().getId()),
                () -> assertEquals("2022-2023", result.get().getName()));
        verify(courseRepository).findById(anyLong());
    }

    @Test
    void Should_ReturnEmptyCourse_When_FindById() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        Optional<Course> result = courseService.findById(anyLong());
        assertAll(() -> assertTrue(result.isEmpty()));
        verify(courseRepository).findById(anyLong());
    }

    @Test
    void Should_ReturnCourse_When_Save() {
        when(courseRepository.save(any())).then(i ->{
            Course c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        Course result = courseService.save(Data.createCourse("2022-2023").get());
        assertAll(() -> assertEquals("2022-2023", result.getName()),
                () -> assertEquals(1l, result.getId()));
        verify(courseRepository).save(any());
    }

    @Test
    void Should_Delete_When_DeleteOneCourse() {
        Optional<Course> course = Data.createCourse("2022-2023");
        doAnswer(i -> {
            Course arg0 = i.getArgument(0);
            assertEquals(arg0.getName(), course.get().getName());
            return null;
        }).when(courseRepository).delete(any());

        courseService.delete(course.get());
        verify(courseRepository).delete(any());
        verify(courseRepository).flush();
    }

    @Test
    void Should_ReturnList_When_FindCoursesTakenByTeacher() {
        when(courseRepository.findCoursesTakenByTeacher(anyLong())).thenReturn(Data.createListCourse("2021-2022", "2022-2023"));
        List<Course> result = courseService.findCoursesTakenByTeacher(anyLong());
        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(c -> c.getCourseTeachers()
                        .stream().anyMatch(ct -> ct.getCourse().getName().equals("2021-2022")))),
                () -> assertTrue(result.stream().anyMatch(c -> c.getCourseTeachers()
                        .stream().anyMatch(ct -> ct.getCourse().getName().equals("2022-2023")))));
        verify(courseRepository).findCoursesTakenByTeacher(anyLong());
    }

    @Test
    void Should_ReturnEmptyList_When_FindCoursesTakenByTeacher() {
        when(courseRepository.findCoursesTakenByTeacher(anyLong())).thenReturn(new ArrayList<>());
        List<Course> result = courseService.findCoursesTakenByTeacher(anyLong());
        assertAll(() -> assertTrue(result.isEmpty()));
        verify(courseRepository).findCoursesTakenByTeacher(anyLong());
    }

    @Test
    void Should_ReturnGlobalStatistics_When_GetGlobalStatistics() {
        Optional<Course> course = Data.createCourse("2022-2023");
        when(subjectRepository.getSumTotalHoursAndSubjectsNumber(anyLong())).thenReturn(Data.createListIntegers(1200, 400));
        when(teacherRepository.findSumCorrectedHoursByCourse(anyLong())).thenReturn(1000);
        when(teacherRepository.findSumChosenHoursByCourse(anyLong())).thenReturn(150);
        when(subjectService.searchByCourse(any(), eq("Conflicto"), anyString(), any(), anyString(), anyString(), anyString(), any())).thenReturn(Collections.emptyList());
        when(subjectService.searchByCourse(any(), eq("Completa"), anyString(), any(), anyString(), anyString(), anyString(), any())).thenReturn(Data.createResultSearch());


        Integer[] result = courseService.getGlobalStatistics(course.get());

        assertAll(() -> assertEquals(9, result.length),
                () -> assertEquals(12, result[0]),
                () -> assertEquals(150, result[1]),
                () -> assertEquals(1200, result[2]),
                () -> assertEquals(15, result[3]),
                () -> assertEquals(1000, result[4]),
                () -> assertEquals(0, result[5]),
                () -> assertEquals(2, result[6]),
                () -> assertEquals(400, result[7]),
                () -> assertEquals(0, result[8]));

        verify(subjectRepository).getSumTotalHoursAndSubjectsNumber(anyLong());
        verify(teacherRepository).findSumCorrectedHoursByCourse(anyLong());
        verify(teacherRepository).findSumChosenHoursByCourse(anyLong());
        verify(subjectService, times(2)).searchByCourse(any(), anyString(), anyString(),  any(), anyString(), anyString(), anyString(), any());
    }

   @Test
    void Should_WriteHeaderAndBodyInCSV_When_WritePODInCSV() throws IOException {
       ByteArrayInputStream stream = courseService.writePODInCSV(Data.createBodyForCSV());

       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
       String line;
       int count = 0;
       while((line = bufferedReader.readLine()) != null){
           String[] values = line.split(";", -1);
           if(count == 0){
               assertEquals("Titulación", values[1]);
               assertEquals("Campus", values[2]);
           } else{
               assertTrue(values[0].equals("4564654") && values[2].equals("Móstoles"));
           }
           count++;
       }

       int finalCount = count;
       assertAll(() -> assertEquals(2, finalCount));
    }

    @Test
    void Should_ReturnBody_When_CreateContentForCSV() {
        List<String[]> result = courseService.createContentForCSV(Data.createResultSearch());

        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(t -> t[0].equals("2323232")
                && t[5].equals("Estructuras de datos") && t[12].equals("[30h Luis Rodriguez]"))),
                () -> assertTrue(result.stream().anyMatch(t -> t[0].equals("4545454")
                && t[5].equals("Bases de datos") && t[12].equals("[]"))));
    }

    @Test
    void Should_ReturnEmptyBody_When_NoContentForCSV() {
        List<String[]> result = courseService.createContentForCSV(new ArrayList<>());
        assertAll(() -> assertEquals(0, result.size()));
    }

}
