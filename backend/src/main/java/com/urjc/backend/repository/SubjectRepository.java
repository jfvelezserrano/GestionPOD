package com.urjc.backend.repository;

import com.urjc.backend.model.POD;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("SELECT su FROM Subject su WHERE su.campus = :#{#subject.campus} AND su.career = :#{#subject.career} " +
            "AND su.code = :#{#subject.code} AND su.name = :#{#subject.name} AND su.quarter = :#{#subject.quarter} " +
            "AND su.title = :#{#subject.title} AND su.totalHours = :#{#subject.totalHours} AND su.turn = :#{#subject.turn} " +
            "AND su.type = :#{#subject.type} AND su.year = :#{#subject.year} ")
    List<Subject> existsSubject(@Param("subject") Subject subject);

    @Query(" SELECT subject, subject.totalHours - SUM(pods.chosenHours) FROM Subject subject JOIN subject.courseSubjects cs JOIN cs.course c LEFT JOIN subject.pods pods" +
            " WHERE c.id = :id AND (pods.course.id=:id OR pods.course IS NULL) group by subject ")
    Page<Object[]> getSubjectsByCourse(@Param("id") Long id, Pageable pageable);

    @Query("SELECT subject FROM Subject subject JOIN subject.courseSubjects cs JOIN cs.course c WHERE c.id = :id ")
    List<Subject> findAllByCourse(@Param("id") Long id);

    @Query("SELECT subject FROM Subject subject  JOIN subject.pods pods JOIN pods.course c JOIN pods.teacher t" +
            " WHERE c.id = :idCourse AND t.id = :idTeacher")
    List<Subject> findAllMySubjects(@Param("idTeacher") Long idTeacher, @Param("idCourse") Long idCourse, Sort typeSort);


    @Query("SELECT DISTINCT subject.title FROM Subject subject")
    List<String> getTitles();

    @Query("SELECT DISTINCT subject.campus FROM Subject subject")
    List<String> getCampus();

    @Query("SELECT DISTINCT subject.type FROM Subject subject")
    List<String> getTypes();
}
