package com.urjc.backend.repository;

import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Teacher findByEmail(String email);

    @Query("SELECT t FROM Teacher t WHERE t.name = :#{#teacher.name} AND t.email = :#{#teacher.email} ")
    Teacher existsTeacher(@Param("teacher") Teacher teacher);

    @Query(value = "SELECT teacher FROM Teacher teacher JOIN teacher.courseTeachers ct JOIN ct.course c WHERE c.id = :id")
    Page<Teacher> getTeachersByCourse(@Param("id") Long id, Pageable pageable);

    @Query("SELECT teacher FROM Teacher teacher JOIN teacher.courseTeachers ct JOIN ct.course c WHERE c.id = :id ")
    List<Teacher> getAllTeachersByCourse(@Param("id") Long id);

    @Query("SELECT t FROM Teacher t JOIN t.roles r WHERE r = :role ")
    List<Teacher> findByRole(@Param("role") String role);
}
