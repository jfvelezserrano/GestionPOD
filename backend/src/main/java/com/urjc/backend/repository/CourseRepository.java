package com.urjc.backend.repository;

import com.urjc.backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findById(Long id);

    List<Course> OrderByCreationDateDesc();

    Optional<Course> findFirst1ByOrderByCreationDateDesc();

    Optional<Course> findByName(String name);

    @Query("SELECT c FROM Teacher teacher JOIN teacher.courseTeachers ct JOIN ct.course c WHERE teacher.id = :idTeacher ORDER BY c.creationDate DESC")
    List<Course> findCoursesTakenByTeacher(@Param("idTeacher") Long idTeacher);
}
