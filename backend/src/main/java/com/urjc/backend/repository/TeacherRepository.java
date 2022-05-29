package com.urjc.backend.repository;

import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Teacher findByEmail(String email);

    @Query("SELECT t FROM Teacher t WHERE t.name = :#{#teacher.name} AND t.email = :#{#teacher.email} ")
    Teacher existsTeacher(@Param("teacher") Teacher teacher);

    @Query("SELECT teacher FROM Teacher teacher JOIN teacher.courseTeachers ct JOIN ct.course c WHERE c.id = :id ")
    List<Teacher> getTeachersByPOD(@Param("id") Long id);

}
