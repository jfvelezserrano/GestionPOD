package com.urjc.backend.repository;

import com.urjc.backend.model.CourseTeacher;
import com.urjc.backend.model.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql({ "/import.sql" })
public class TeacherRepositoryTest {
    @Autowired
    TeacherRepository teacherRepository;

    @Test
    void Should_ReturnCorrectedHoursAndObservation_When_FindEditableData() {
        Object[] editableData = teacherRepository.findEditableData(3l, 1l).get(0);
        assertAll(() -> assertEquals(2, editableData.length),
                () -> assertEquals(130, editableData[0]),
                () -> assertEquals("Elegir una asigmatura más", editableData[1]));
    }

    @Test
    void Should_ReturnTeachers_When_FindByCourse() {
        List<Teacher> teachers = teacherRepository.findByCourse(1l);
        assertAll(() -> assertEquals(2, teachers.size()),
                () -> assertEquals("aliciaholi36@gmail.com", teachers.get(0).getEmail()));
    }

    @Test
    void Should_ReturnTeachersInPage_When_RequestTeachersByCourseAndPage() {
        Pageable pageable = PageRequest.of(0, 12, Sort.unsorted());
        List<Teacher> teachers = teacherRepository.findByCoursePage(1l, pageable).stream().toList();
        assertAll(() -> assertEquals(2, teachers.size()),
                () -> assertEquals("aliciaholi36@gmail.com", teachers.get(0).getEmail()));
    }

    @Test
    void Should_ReturnTeachers_When_FindByRole() {
        List<Teacher> teachers = teacherRepository.findByRole("ADMIN");
        assertAll(() -> assertEquals(1, teachers.size()),
                () -> assertEquals("aliciaholi36@gmail.com", teachers.get(0).getEmail()));
    }

    @Test
    void Should_ReturnStatistics_When_RequestStatisticsByTeacherAndCourse() {
        Integer[] statistics = teacherRepository.statisticsByTeacherAndCourse(3l, 1l).get(0);
        assertAll(() -> assertEquals(3, statistics.length),
                () -> assertEquals(0, statistics[0]),
                () -> assertEquals(50, statistics[1]),
                () -> assertEquals(1, statistics[2]));
    }

    @Test
    void Should_ReturnMates_When_FindMatesByTeacherAndCourse() {
        List<Object[]> mates = teacherRepository.findMatesByTeacherAndCourse(3l, 1l);
        assertAll(() -> assertEquals(1, mates.size()),
                () -> assertEquals(4, mates.get(0).length),
                () -> assertEquals(6l, mates.get(0)[0]),
                () -> assertEquals("Luis Rodriguez", mates.get(0)[1]),
                () -> assertEquals("Asignatura 2", mates.get(0)[2]),
                () -> assertEquals(115, mates.get(0)[3]));
    }

    @Test
    void Should_ReturnTeachersAndJoinedCourseTeacher_When_FindStatisticsByCourseAndPage() {
        Pageable pageable = PageRequest.of(0, 12, Sort.unsorted());
        List<Object[]> teachers = teacherRepository.findStatisticsByCourseAndPage(1l, pageable).stream().toList();
        CourseTeacher ct1 = ((CourseTeacher) teachers.stream()
                .filter(t -> ((Teacher) t[0]).getEmail().equals("ejemplo@gmail.com"))
                .findFirst().get()[1]);

        CourseTeacher ct2 = ((CourseTeacher) teachers.stream()
                .filter(t -> ((Teacher) t[0]).getEmail().equals("aliciaholi36@gmail.com"))
                .findFirst().get()[1]);

        assertAll(() -> assertEquals(2, teachers.size()),
                () -> assertTrue(teachers.stream()
                        .anyMatch(t -> ((Teacher) t[0]).getEmail().equals("ejemplo@gmail.com") && ct1.getCorrectedHours() == 115
                        && ct1.getObservation().equals("Tengo horas de más") && ct1.getOriginalHours() == 90)),
                () -> assertTrue(teachers.stream()
                        .anyMatch(t -> ((Teacher) t[0]).getEmail().equals("aliciaholi36@gmail.com") && ct2.getCorrectedHours() == 130
                                && ct2.getObservation().equals("Elegir una asigmatura más") && ct2.getOriginalHours() == 100)));
    }

    @Test
    void Should_ReturnChosenHours_When_FindChosenHoursByTeacherAndCourse() {
        Integer chosenHours = teacherRepository.findChosenHoursByTeacherAndCourse(3l, 1l);
        assertAll(() -> assertEquals(50, chosenHours));
    }

    @Test
    void Should_ReturnSumChosenHours_When_FindSumChosenHoursByCourse() {
        Integer sumChosenHours = teacherRepository.findSumChosenHoursByCourse(1l);
        assertAll(() -> assertEquals(80, sumChosenHours));
    }

    @Test
    void Should_ReturnTotalForce_When_FindSumCorrectedHoursByCourse() {
        Integer sumCorrectedHours = teacherRepository.findSumCorrectedHoursByCourse(1l);
        assertAll(() -> assertEquals(245, sumCorrectedHours));
    }
}
