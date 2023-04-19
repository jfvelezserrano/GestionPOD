package com.urjc.backend.repository;

import com.urjc.backend.model.Subject;
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
class SubjectRepositoryTest {

    @Autowired
    SubjectRepository subjectRepository;

    @Test
    void Should_ReturnSubjects_When_FindSimilarSubjects() {
        Subject subject = subjectRepository.findById(4l).get();
        List<Subject> subjects = subjectRepository.findSameValues(subject);
        assertAll(() -> assertEquals(1, subjects.size()));
    }

    @Test
    void Should_ReturnSubjectsInPage_When_RequestSubjectsByCourseAndPage() {
        Pageable pageable = PageRequest.of(0, 12, Sort.unsorted());
        List<Subject> subjects = subjectRepository.findByCoursePage(1l, pageable).stream().toList();
        assertAll(() -> assertEquals(2, subjects.size()),
                () -> assertTrue(subjects.stream().anyMatch(s -> s.getId() == 4l)),
                () -> assertTrue(subjects.stream().anyMatch(s -> s.getId() == 5l)));
    }

    @Test
    void Should_ReturnSubjects_When_SearchSubjectsBySpecificValues() {
        List<Subject> subjects = subjectRepository.search(1l, "Segundo Cuatrimestre", 'T', null, null, null, Sort.unsorted());
        assertAll(() -> assertEquals(1, subjects.size()),
                () -> assertEquals(5l, subjects.get(0).getId()));
    }

    @Test
    void Should_ReturnSubjects_When_FindSubjectsByCourse() {
        List<Subject> subjects = subjectRepository.findByCourse(1l);
        assertAll(() -> assertEquals(2, subjects.size()),
                () -> assertTrue(subjects.stream().anyMatch(s -> s.getId() == 4l)),
                () -> assertTrue(subjects.stream().anyMatch(s -> s.getId() == 5l)));
    }

    @Test
    void Should_ReturnSubject_When_FindSubjectByCourseAndCode() {
        Subject subject = subjectRepository.findByCourseAndCode(1l, "12135127");
        assertAll(() -> assertEquals(5l, subject.getId()),
                () -> assertEquals("12135127", subject.getCode()));
    }

    @Test
    void Should_ReturnSubjects_When_FindSubjectsByCourseAndTeacher() {
        List<Subject> subjects = subjectRepository.findByCourseAndTeacher(1l, 3l, Sort.unsorted());
        assertAll(() -> assertEquals(1, subjects.size()));
    }

    @Test
    void Should_ReturnTitles_When_RequestAllTitles() {
        List<String> titles = subjectRepository.getTitles();
        assertAll(() -> assertEquals(2, titles.size()),
                () -> assertTrue(titles.stream().anyMatch(t -> t.equals("(2034) Grado Ingeniería Software (M)"))),
                () -> assertTrue(titles.stream().anyMatch(t -> t.equals("(2034) Grado Ingeniería Informática (T)"))));
    }

    @Test
    void Should_ReturnTitles_When_RequestAllTitlesByCourse() {
        List<String> titles = subjectRepository.getTitlesByCourse(1l);
        assertAll(() -> assertEquals(2, titles.size()),
                () -> assertTrue(titles.stream().anyMatch(t -> t.equals("(2034) Grado Ingeniería Software (M)"))),
                () -> assertTrue(titles.stream().anyMatch(t -> t.equals("(2034) Grado Ingeniería Informática (T)"))));
    }

    @Test
    void Should_ReturnSubjectsName_When_RequestAllSubjectsByCourse() {
        List<String> subjectsName = subjectRepository.getSubjectsByCourse(1l);
        assertAll(() -> assertEquals(2, subjectsName.size()),
                () -> assertTrue(subjectsName.stream().anyMatch(s -> s.equals("Asignatura 1"))),
                () -> assertTrue(subjectsName.stream().anyMatch(s -> s.equals("Asignatura 2"))));
    }

    @Test
    void Should_ReturnCampus_When_RequestAllCampus() {
        List<String> campus = subjectRepository.getCampus();
        assertAll(() -> assertEquals(2, campus.size()),
                () -> assertTrue(campus.stream().anyMatch(c -> c.equals("Móstoles"))),
                () -> assertTrue(campus.stream().anyMatch(c -> c.equals("Fuenlabrada"))));
    }

    @Test
    void Should_ReturnTypes_When_RequestAllTypes() {
        List<String> types = subjectRepository.getTypes();
        assertAll(() -> assertEquals(2, types.size()),
                () -> assertTrue(types.stream().anyMatch(t -> t.equals("Obligatoria"))),
                () -> assertTrue(types.stream().anyMatch(t -> t.equals("Optativa"))));
    }

    @Test
    void Should_ReturnBarGraphData_When_RequestHoursPerSubject() {
        List<Object[]> barGraphData = subjectRepository.hoursPerSubjectByTeacherAndCourse(3l, 1l, Sort.unsorted());
        assertAll(() -> assertEquals(1, barGraphData.size()),
                () -> assertEquals("Asignatura 2", barGraphData.get(0)[0]),
                () -> assertEquals(150, ((Integer) barGraphData.get(0)[1])),
                () -> assertEquals(50, ((Integer) barGraphData.get(0)[2])));
    }

    @Test
    void Should_ReturnDoughnutChartData_When_RequestPercentageHours() {
        List<Object[]> doughnutChartData = subjectRepository.percentageHoursByTeacherAndCourse(3l, 1l, 50, Sort.unsorted());
        assertAll(() -> assertEquals(1, doughnutChartData.size()),
                () -> assertEquals("Asignatura 2", doughnutChartData.get(0)[0]),
                () -> assertEquals(100, ((Integer) doughnutChartData.get(0)[1])));
    }

    @Test
    void Should_ReturnSumChosenHours_When_RequestTotalChosenHours() {
        Integer sum = subjectRepository.totalChosenHoursByTeacherAndCourse(3l, 1l);
        assertAll(() -> assertEquals(50, sum));
    }

    @Test
    void Should_ReturnArray_When_RequestTotalHoursAndNumberOfSubjects() {
        Integer[] sum = subjectRepository.getSumTotalHoursAndSubjectsNumber(1l).get(0);
        Integer totalCharge = sum[0];
        Integer numSubjects = sum[1];
        assertAll(() -> assertEquals(2, sum.length),
                () -> assertEquals(330, totalCharge),
                () -> assertEquals(2, numSubjects));
    }
}