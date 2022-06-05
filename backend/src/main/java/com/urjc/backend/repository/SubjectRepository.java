package com.urjc.backend.repository;

import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /*@Modifying
    @Transactional
    @Query("update Subject s set s.active = false where s.active = true")
    void setSubjectsToInactive();*/

    @Query("SELECT su FROM Subject su WHERE su.campus = :#{#subject.campus} AND su.career = :#{#subject.career} " +
            "AND su.code = :#{#subject.code} AND su.name = :#{#subject.name} AND su.quarter = :#{#subject.quarter} " +
            "AND su.title = :#{#subject.title} AND su.totalHours = :#{#subject.totalHours} AND su.turn = :#{#subject.turn} " +
            "AND su.type = :#{#subject.type} AND su.year = :#{#subject.year} ")
    List<Subject> existsSubject(@Param("subject") Subject subject);

    @Query("SELECT subject FROM Subject subject JOIN subject.courseSubjects cs JOIN cs.course c WHERE c.id = :id ")
    Page<Subject> getSubjectsByPOD(@Param("id") Long id, Pageable pageable);

    @Query("SELECT DISTINCT subject.title FROM Subject subject")
    List<String> getTitles();

    @Query("SELECT DISTINCT subject.campus FROM Subject subject")
    List<String> getCampus();

    @Query("SELECT DISTINCT subject.type FROM Subject subject")
    List<String> getTypes();
}
