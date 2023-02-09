package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface SubjectService {
    Subject save(Subject subject);

    void delete(Subject subject);

    Optional<Subject> findById(Long id);

    List<Object[]> findByCoursePage(Course course, Pageable pageable);

    List<Object[]> searchByCourse(Course course, String occupation, String quarter, Character turn, String title,
                                  String emailTeacher, String subjectName, Sort sort);

    List<Subject> findByCourse(Long id);

    boolean isCodeInCourse(Long id, String code);

    void deleteSubjectsByCourse(Course course);

    List<Subject> findByCourseAndTeacher(Long idTeacher, Course course, Sort typeSort);

    List<Object[]> hoursPerSubjectByTeacherAndCourse(Teacher teacher, Course course, Sort typeSort);

    List<Object[]> percentageHoursByTeacherAndCourse(Teacher teacher, Course course, Sort typeSort);

    List<Object[]> findConflictsByTeacherAndCourse(Long idTeacher, Course course, Sort typeSort);

    void saveAll(InputStream inputStream, Course course) throws IOException;

    Subject findSubjectIfExists(Subject subject);

    List<String> getTitles();

    List<String> getTitlesByCourse(Long idCourse);

    List<String> getSubjectsByCourse(Long idCourse);

    List<String> getCampus();

    List<String> getTypes();
}
