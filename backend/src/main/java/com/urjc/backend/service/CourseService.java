package com.urjc.backend.service;

import com.urjc.backend.model.Course;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    boolean exists(String courseName);

    List<Course> findAllOrderByCreationDate();
    Optional<Course> findLastCourse();
    Optional<Course> findById(Long id);
    List<Course> findByTeacherOrderByCreationDate(Long idTeacher);

    Integer[] getGlobalStatistics(Course course);

    Course save(Course course);

    void delete(Course course);

    ByteArrayInputStream writePODInCSV(List<String[]> body) throws IOException;
    List<String[]> createContentForCSV(List<Object[]> subjectsAndTeachersCurrentCourse);
}
