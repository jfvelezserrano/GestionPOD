package com.urjc.backend.repository;

import com.urjc.backend.model.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql({ "/import.sql" })
class CourseRepositoryTest {

    @Autowired
    CourseRepository courseRepository;

    @Test
    void Should_ReturnCourses_When_FindCoursesTakenByTeacher() {
        List<Course> courses = courseRepository.findCoursesTakenByTeacher(3l);
        assertAll(() -> assertEquals(2, courses.size()),
                () -> assertEquals("2022-2023", courses.get(0).getName()),
                () -> assertEquals("2021-2022", courses.get(1).getName()));
    }
}
