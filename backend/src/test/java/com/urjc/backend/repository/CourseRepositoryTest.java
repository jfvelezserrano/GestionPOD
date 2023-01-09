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
public class CourseRepositoryTest {

    @Autowired
    CourseRepository courseRepository;

    @Test
    void Should_ReturnCourses_When_FindCoursesTakenByTeacher() {
        List<Course> courses = courseRepository.findCoursesTakenByTeacher(3l);
        assertAll(() -> assertEquals(2, courses.size()),
                () -> assertTrue(courses.get(0).getName().equals("2022-2023")),
                () -> assertTrue(courses.get(1).getName().equals("2021-2022")));
    }
}
