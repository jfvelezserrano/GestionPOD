package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SubjectService {
    Subject save(Subject subject);

    void delete(Subject subject);

    Optional<Subject> findById(Long id);

    List<Object[]> findByCoursePage(Course course, Pageable pageable);

    List<Object[]> searchByCourse(Course course, String occupation, String quarter, Character turn, String title, String emailTeacher, Sort sort);

    List<Subject> findByCourse(Long id);

    Boolean isCodeInCourse(Long id, String code);

    void deleteSubjectsByCourse(Course course);

    List<Subject> findNameAndQuarterByTeacherAndCourse(Long idTeacher, Course course, Sort typeSort);

    List<Object[]> hoursPerSubjectByTeacherAndCourse(Teacher teacher, Course course, Sort typeSort);

    List<Object[]> percentageHoursByTeacherAndCourse(Teacher teacher, Course course, Sort typeSort);

    List<Object[]> findByTeacherAndCourse(Long idTeacher, Course course, Sort typeSort);

    void saveAll(MultipartFile file, Course course) throws IOException;

    Subject findSubjectIfExists(Subject subject);

    List<String> getTitles();

    List<String> getTitlesByCourse(Long idCourse);

    List<String> getCampus();

    List<String> getTypes();
}
