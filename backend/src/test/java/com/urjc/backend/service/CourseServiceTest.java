package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.repository.CourseRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    CourseRepository courseRepository;

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
                () -> assertEquals("2022-2023", result.get(0).getName()),
                () -> assertEquals("2021-2022", result.get(1).getName()),
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
        Optional<Course> result = courseService.findById(anyLong());
        assertAll(() -> assertTrue(result.isPresent()),
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
    void Should_WriteHeaderAndBodyInCSV_When_WritePODInCSV() throws IOException {
       ByteArrayInputStream stream = courseService.writePODInCSV(Data.createBodyForCSV());

       BufferedReader bfReader = new BufferedReader(new InputStreamReader(stream));
       String line;
       int count = 0;
       while((line = bfReader.readLine()) != null){
           String[] values = line.split(";", -1);
           if(count == 0){
               assertTrue(values[1].equals("Titulación"));
               assertTrue(values[2].equals("Campus"));
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
