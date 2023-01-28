package com.urjc.backend.service;

import com.urjc.backend.Data;
import com.urjc.backend.error.exception.CSVValidationException;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.CourseRepository;
import com.urjc.backend.repository.SubjectRepository;
import com.urjc.backend.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.urjc.backend.service.DataServices.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    TeacherRepository teacherRepository;

    @Mock
    SubjectRepository subjectRepository;

    @Mock
    CourseRepository courseRepository;

    @InjectMocks
    TeacherServiceImpl teacherService;

    private static final String emailMainAdmin = "a.merinom.2017@alumnos.urjc.es";
    private static final String nameMainAdmin = "Alicia Merino Martínez";

    @Test
    void Should_ReturnNull_When_TeacherNotExists() {
        when(teacherRepository.findByEmail(anyString())).thenReturn(null);
        Teacher result = teacherService.findIfIsInCurrentCourse( "ejemplo@ejemplo.com");
        assertAll(() -> assertNull(result));
        verify(teacherRepository).findByEmail(anyString());
        verify(courseRepository, never()).findFirst1ByOrderByCreationDateDesc();
    }

    @Test
    void Should_ReturnNull_When_NotInCurrentCourse() {
        when(teacherRepository.findByEmail(anyString())).thenReturn(Data.createTeacher("Pedro López", "ejemplo2@ejemplo2.com").get());
        when(courseRepository.findFirst1ByOrderByCreationDateDesc()).thenReturn(Data.createCourse("2022-2023"));
        Teacher result = teacherService.findIfIsInCurrentCourse( "ejemplo@ejemplo.com");
        assertAll(() -> assertNull(result));
        verify(teacherRepository).findByEmail(anyString());
        verify(courseRepository).findFirst1ByOrderByCreationDateDesc();
    }

    @Test
    void Should_ReturnTeacher_When_FindIfIsInCurrentCourse() {
        Optional<Course> course = Data.createCourse("2022-2023");
        Optional<Teacher> teacher = Data.createTeacher("Pedro López", "ejemplo2@ejemplo2.com");
        course.get().addTeacher(teacher.get(), 120);
        when(teacherRepository.findByEmail(anyString())).thenReturn(teacher.get());
        when(courseRepository.findFirst1ByOrderByCreationDateDesc()).thenReturn(course);
        Teacher result = teacherService.findIfIsInCurrentCourse( "ejemplo2@ejemplo2.com");
        assertAll(() -> assertEquals("ejemplo2@ejemplo2.com", result.getEmail()));
        verify(teacherRepository).findByEmail(anyString());
        verify(courseRepository).findFirst1ByOrderByCreationDateDesc();
    }

    @Test
    void Should_ReturnAdmin_When_FindIfIsInCurrentCourse() {
        when(teacherRepository.findByEmail(anyString())).thenReturn(Data.createAdmin(nameMainAdmin, emailMainAdmin).get());
        Teacher result = teacherService.findIfIsInCurrentCourse( emailMainAdmin);
        assertAll(() -> assertEquals(emailMainAdmin, result.getEmail()));
        verify(teacherRepository).findByEmail(anyString());
        verify(courseRepository, never()).findFirst1ByOrderByCreationDateDesc();
    }

    @Test
    void Should_ReturnTeacher_When_FindByEmail() {
        when(teacherRepository.findByEmail(anyString())).thenReturn(Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get());
        Teacher result = teacherService.findByEmail("ejemplo@ejemplo.com");
        assertAll(() -> assertEquals("Luis Rodriguez", result.getName()),
                () -> assertEquals("ejemplo@ejemplo.com", result.getEmail()));
        verify(teacherRepository).findByEmail(anyString());
    }

    @Test
    void Should_DeleteTeacherFromDDBB_When_DeleteByCourse() {
        ReflectionTestUtils.setField(teacherService, "emailMainAdmin", emailMainAdmin);

        Optional<Course> course = Data.createCourse("2022-2023");
        when(teacherRepository.findByCourse(anyLong())).thenReturn(Data.createListTeacher());

        doAnswer(i -> {
            Teacher arg0 = i.getArgument(0);
            assertEquals("Luis Rodriguez", arg0.getName());
            return null;
        }).when(teacherRepository).delete(any());

        doAnswer(i -> {
            Teacher arg0 = i.getArgument(0);
            assertEquals("Pedro López", arg0.getName());
            return null;
        }).when(teacherRepository).save(any());

        teacherService.deleteTeachersByCourse(course.get());

        verify(teacherRepository).findByCourse(anyLong());
        verify(teacherRepository).delete(any());
        verify(teacherRepository).save(any());
    }

    @Test
    void Should_DoNothing_When_NotExistTeachersInCourse() {
        Optional<Course> course = Data.createCourse("2023-2024");
        when(teacherRepository.findByCourse(anyLong())).thenReturn(Collections.emptyList());
        teacherService.deleteTeachersByCourse(course.get());
        verify(teacherRepository, never()).delete(any());
        verify(teacherRepository, never()).save(any());
    }

    @Test
    void Should_ReturnEditableData_When_GetEditableData() {
        Optional<Course> course = Data.createCourse("2023-2024");
        Optional<Teacher> teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com");
        List<Object[]> editableDataList = Data.createListObject(120, null);
        course.get().addTeacher(teacher.get(), 120);

        when(teacherRepository.findByEmail(anyString())).thenReturn(teacher.get());
        when(teacherRepository.findEditableData(anyLong(), anyLong())).thenReturn(editableDataList);

        Object[] result = teacherService.getEditableData("ejemplo@ejemplo.com", course.get());

        assertAll(() -> assertEquals(editableDataList.get(0)[0], result[0]),
                () -> assertEquals(editableDataList.get(0)[1], result[1]));

        verify(teacherRepository).findByEmail(anyString());
        verify(teacherRepository).findEditableData(anyLong(), anyLong());
    }

    @Test
    void Should_Delete_When_DeleteOneTeacher() {
        Optional<Teacher> teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com");
        doAnswer(i -> {
            Teacher arg0 = i.getArgument(0);
            assertEquals(arg0.getName(), teacher.get().getName());
            return null;
        }).when(teacherRepository).delete(any());

        teacherService.delete(teacher.get());
        verify(teacherRepository).delete(any());
    }

    @Test
    void Should_DoNothing_When_LastCourseNotExists() {
        when(courseRepository.findFirst1ByOrderByCreationDateDesc()).thenReturn(Optional.empty());
        teacherService.updateAdminsInLastCourse();
        verify(teacherRepository, never()).findByRole(anyString());
        verify(teacherRepository, never()).save(any());
    }

    @Test
    void Should_DeleteRoleAdminAndAddMainAdminToCourse_When_AdminNotInLastCourse() {
        ReflectionTestUtils.setField(teacherService, "emailMainAdmin", emailMainAdmin);

        Teacher mainAdmin = Data.createAdmin(nameMainAdmin, emailMainAdmin).get();
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(Data.createAdmin("Paula Martínez", "admin@admin.com").get());
        teachers.add(mainAdmin);
        Optional<Course> course = Data.createCourse("2022-2023");

        when(courseRepository.findFirst1ByOrderByCreationDateDesc()).thenReturn(course);
        when(teacherRepository.findByRole(anyString())).thenReturn(teachers);

        doAnswer(i -> {
            Teacher arg0 = i.getArgument(0);
            assertAll(() -> assertEquals("admin@admin.com", arg0.getEmail()),
                    () -> assertEquals(1, arg0.getRoles().size()));

            return null;
        }).when(teacherRepository).save(any());

        teacherService.updateAdminsInLastCourse();

        assertAll(() -> assertTrue(course.get().isTeacherInCourse(mainAdmin)));

        verify(teacherRepository).findByRole(anyString());
        verify(teacherRepository).save(any());
    }

    @Test
    void Should_ReturnTeacher_When_Save() {
        when(teacherRepository.save(any())).then(i ->{
            Teacher t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        Teacher result = teacherService.save(Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get());
        assertAll(() -> assertEquals("Luis Rodriguez", result.getName()),
                () -> assertEquals("ejemplo@ejemplo.com", result.getEmail()));
        verify(teacherRepository).save(any());
    }

    @Test
    void Should_ReturnTeachersInPage_When_RequestTeachersByCourseAndPage() {
        Pageable pageable = PageRequest.of(0, 12, Sort.unsorted());
        when(teacherRepository.findByCoursePage(anyLong(), any())).thenReturn(new PageImpl<>(Data.createListTeacher()));

        List<Teacher> result = teacherService.findAllByCourse(1l, pageable);
        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(s -> s.getName().equals("Luis Rodriguez"))),
                () -> assertTrue(result.stream().anyMatch(s -> s.getName().equals("Pedro López"))));

        verify(teacherRepository).findByCoursePage(anyLong(), any());
    }

    @Test
    void Should_ReturnStatistics_When_RequestAllTeachersStatistics() {
        Optional<Course> course = Data.createCourse("2022-2023");
        Pageable pageable = PageRequest.of(0, 12, Sort.unsorted());
        when(teacherRepository.findStatisticsByCourseAndPage(anyLong(), any())).thenReturn(new PageImpl<>(createStatisticsAllTeachers()));
        when(teacherRepository.statisticsByTeacherAndCourse(anyLong(), anyLong())).thenReturn(statisticsByTeacherAndCourse());

        List<Object[]> result = teacherService.allTeachersStatistics(course.get(), pageable);

        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(st -> st[0].equals("Luis Rodriguez")
                                && ((int) st[2]) == 40 && ((int) st[3]) == 100 && ((int) st[4]) == 2)));

        verify(teacherRepository, atLeastOnce()).findStatisticsByCourseAndPage(anyLong(), any());
        verify(teacherRepository, atLeastOnce()).statisticsByTeacherAndCourse(anyLong(), anyLong());
    }

    @Test
    void Should_ReturnEmptyList_When_NotFoundStatistics() {
        Optional<Course> course = Data.createCourse("2022-2023");
        Pageable pageable = PageRequest.of(0, 12, Sort.unsorted());
        when(teacherRepository.findStatisticsByCourseAndPage(anyLong(), any())).thenReturn(Page.empty());

        List<Object[]> result = teacherService.allTeachersStatistics(course.get(), pageable);

        assertAll(() -> assertTrue(result.isEmpty()));

        verify(teacherRepository).findStatisticsByCourseAndPage(anyLong(), any());
        verify(teacherRepository, never()).statisticsByTeacherAndCourse(anyLong(), anyLong());
    }

    @Test
    void Should_ReturnTeachers_When_FindByRole() {
        when(teacherRepository.findByRole(anyString())).thenReturn(Data.createListTeacher());
        List<Teacher> result = teacherService.findAllByRole("TEACHER");
        assertAll(() -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().anyMatch(s -> s.getRoles().contains("TEACHER"))));
        verify(teacherRepository).findByRole(anyString());
    }

    @Test
    void Should_ReturnTeacher_When_FindById() {
        when(teacherRepository.findById(anyLong())).thenReturn(Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com"));
        Optional<Teacher> result = teacherService.findById(1l);
        assertAll(() -> assertTrue(result.get().getRoles().contains("TEACHER")));
        verify(teacherRepository).findById(anyLong());
    }

    @Test
    void Should_ReturnStatistics_When_FindPersonalStatistics() {
        Optional<Course> course = Data.createCourse("2022-2023");
        when(teacherRepository.statisticsByTeacherAndCourse(anyLong(), anyLong())).thenReturn(statisticsByTeacherAndCourse());
        when(subjectRepository.findByCourseAndTeacher(anyLong(), anyLong(), any())).thenReturn(Data.createListSubject());
        when(teacherRepository.findEditableData(anyLong(), anyLong())).thenReturn(Data.createListObject(120, null));

        Integer[] result = teacherService.findPersonalStatistics(1l, course.get());
        assertAll(() -> assertEquals(40, result[0]),
                () -> assertEquals(100, result[1]),
                () -> assertEquals(120, result[2]),
                () -> assertEquals(2, result[3]),
                () -> assertEquals(0, result[4]));

        verify(teacherRepository).statisticsByTeacherAndCourse(anyLong(), anyLong());
        verify(subjectRepository).findByCourseAndTeacher(anyLong(), anyLong(), any());
        verify(teacherRepository).findEditableData(anyLong(), anyLong());
    }

    @Test
    void Should_ReturnMates_When_FindMates() {
        when(teacherRepository.findMatesByTeacherAndCourse(anyLong(), anyLong())).thenReturn(Data.createListObject(1l, "Luis Rodriguez", "Estadística", 100));
        when(teacherRepository.findChosenHoursByTeacherAndCourse(anyLong(), anyLong())).thenReturn(60);

        List<Object[]> result = teacherService.findMates(1l, 1l);

        assertAll(() -> assertTrue(result.stream().anyMatch(m -> m[0].equals("Luis Rodriguez")
                && m[1].equals("Estadística") && ((int) m[2]) == 60)));

        verify(teacherRepository).findMatesByTeacherAndCourse(anyLong(), anyLong());
        verify(teacherRepository).findChosenHoursByTeacherAndCourse(anyLong(), anyLong());
    }

    @Test
    void Should_ReturnEmptyList_When_NotFindMates() {
        when(teacherRepository.findMatesByTeacherAndCourse(anyLong(), anyLong())).thenReturn(Collections.emptyList());
        List<Object[]> result = teacherService.findMates(1l, 1l);
        assertAll(() -> assertTrue(result.isEmpty()));
        verify(teacherRepository).findMatesByTeacherAndCourse(anyLong(), anyLong());
        verify(teacherRepository, never()).findChosenHoursByTeacherAndCourse(anyLong(), anyLong());
    }

    @Test
    void Should_SaveTeachersInDDBB_When_SaveFileTeachers() throws IOException {
        when(teacherRepository.findByEmail(anyString())).thenReturn(null);
        when(teacherRepository.save(any())).then(i ->{
            Teacher t = i.getArgument(0);
            assertAll(() -> assertTrue(t.getEmail().equals("ejemplo@ejemplo.com")
                    || t.getEmail().equals("facundo@ejemplo.com") || t.getEmail().equals("camilo@ejemplo.com")));
            return t;
        });

        Optional<Course> course = Data.createCourse("2022-2023");
        teacherService.saveAll(Data.createInputStreamTeacher(), course.get());

        verify(teacherRepository, times(3)).findByEmail(anyString());
        verify(teacherRepository, times(3)).save(any());
    }

    @Test
    void Should_ThrowException_When_SaveWrongFileTeachers() {
        Optional<Course> course = Data.createCourse("2022-2023");

        assertThrows(CSVValidationException.class, () -> {
            teacherService.saveAll(Data.createInputStreamTeacherError(), course.get());
        });

        verify(teacherRepository, never()).findByEmail(anyString());
        verify(teacherRepository, never()).save(any());
    }

    @Test
    void Should_DoNothing_When_FileTeachersIsEmpty() throws IOException {
        Optional<Course> course = Data.createCourse("2022-2023");
        teacherService.saveAll(Data.createEmptyInputStream(), course.get());
        verify(teacherRepository, never()).findByEmail(anyString());
        verify(teacherRepository, never()).save(any());
    }
}
