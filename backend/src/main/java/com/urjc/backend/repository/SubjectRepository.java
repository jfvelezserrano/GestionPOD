package com.urjc.backend.repository;

import com.urjc.backend.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long>{

    @Query("SELECT su FROM Subject su WHERE su.campus = :#{#subject.campus} AND su.career = :#{#subject.career} " +
            "AND su.code = :#{#subject.code} AND su.name = :#{#subject.name} AND su.quarter = :#{#subject.quarter} " +
            "AND su.title = :#{#subject.title} AND su.totalHours = :#{#subject.totalHours} AND su.turn = :#{#subject.turn} " +
            "AND su.type = :#{#subject.type} AND su.year = :#{#subject.year} ")
    List<Subject> findSameValues(@Param("subject") Subject subject);

    @Query(" SELECT subject FROM Subject subject JOIN subject.courseSubjects cs JOIN cs.course c LEFT JOIN subject.pods pods" +
            " WHERE c.id = :id group by subject ")
    Page<Subject> findByCoursePage(@Param("id") Long id, Pageable pageable);

    @Query(" SELECT subject FROM Subject subject JOIN subject.courseSubjects cs JOIN cs.course c LEFT JOIN subject.pods pods " +
            "LEFT JOIN pods.course pc LEFT JOIN pods.teacher pt WHERE c.id = :idCourse AND " +
            "(:quarter IS NULL OR subject.quarter = :quarter) AND (:turn IS NULL OR subject.turn = :turn) " +
            "AND (:title IS NULL OR subject.title = :title) AND (:emailTeacher IS NULL OR (pt.email = :emailTeacher AND pc.id=:idCourse)) " +
            "AND (:subjectName IS NULL OR subject.name = :subjectName) " +
            "group by subject ")
    List<Subject> search(@Param("idCourse") Long idCourse, @Param("quarter") String quarter, @Param("turn") Character turn,
                         @Param("title") String title, @Param("emailTeacher") String emailTeacher, String subjectName, Sort sort);

    @Query("SELECT subject FROM Subject subject JOIN subject.courseSubjects cs JOIN cs.course c WHERE c.id = :id ")
    List<Subject> findByCourse(@Param("id") Long id);

    @Query("SELECT subject FROM Subject subject JOIN subject.courseSubjects cs JOIN cs.course c WHERE c.id = :id AND subject.code = :code ")
    Subject findByCourseAndCode(@Param("id") Long id, @Param("code") String code);

    @Query("SELECT subject FROM Subject subject JOIN subject.pods pods JOIN pods.course c JOIN pods.teacher t" +
            " WHERE c.id = :idCourse AND t.id = :idTeacher")
    List<Subject> findByCourseAndTeacher(@Param("idCourse") Long idCourse, @Param("idTeacher") Long idTeacher, Sort typeSort);

    @Query("SELECT DISTINCT subject.title FROM Subject subject")
    List<String> getTitles();

    @Query("SELECT DISTINCT subject.title FROM Subject subject JOIN subject.courseSubjects cs WHERE cs.course.id = :idCourse")
    List<String> getTitlesByCourse(@Param("idCourse") Long idCourse);

    @Query("SELECT DISTINCT subject.name FROM Subject subject JOIN subject.courseSubjects cs WHERE cs.course.id = :idCourse")
    List<String> getSubjectsByCourse(@Param("idCourse") Long idCourse);

    @Query("SELECT DISTINCT subject.campus FROM Subject subject")
    List<String> getCampus();

    @Query("SELECT DISTINCT subject.type FROM Subject subject")
    List<String> getTypes();

    @Query("SELECT subject.name, subject.totalHours, pods.chosenHours FROM Subject subject JOIN subject.pods pods JOIN pods.course c JOIN pods.teacher t" +
            " WHERE c.id = :idCourse AND t.id = :idTeacher")
    List<Object[]> hoursPerSubjectByTeacherAndCourse(Long idTeacher, Long idCourse, Sort typeSort);

    @Query("SELECT subject.name, (pods.chosenHours/:total)*100 as percentage FROM Subject subject JOIN subject.pods pods JOIN pods.course c JOIN pods.teacher t" +
            " WHERE c.id = :idCourse AND t.id = :idTeacher")
    List<Object[]> percentageHoursByTeacherAndCourse(Long idTeacher, Long idCourse, Integer total, Sort typeSort);

    @Query("SELECT SUM(pods.chosenHours) FROM Subject subject JOIN subject.pods pods JOIN pods.course c JOIN pods.teacher t" +
            " WHERE c.id = :idCourse AND t.id = :idTeacher")
    Integer totalChosenHoursByTeacherAndCourse(Long idTeacher, Long idCourse);

    @Query("SELECT SUM(subject.totalHours) as totalCharge, COUNT(subject.id) as numSubjects FROM Subject subject " +
            "JOIN subject.courseSubjects sc JOIN sc.course c WHERE c.id = :idCourse")
    List<Integer[]> getSumTotalHoursAndSubjectsNumber(Long idCourse);

}
