package com.urjc.backend.repository;

import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Teacher findByEmail(String email);

    @Query("SELECT ct.correctedHours, ct.observation FROM Teacher t JOIN t.courseTeachers ct JOIN ct.course c WHERE t.email = :email AND c.id = :idCourse ")
    Object getEditableData(String email, Long idCourse);

    @Query("SELECT t FROM Teacher t WHERE t.name = :#{#teacher.name} AND t.email = :#{#teacher.email} ")
    Teacher exists(@Param("teacher") Teacher teacher);

    @Query("SELECT teacher FROM Teacher teacher JOIN teacher.courseTeachers ct JOIN ct.course c WHERE c.id = :id ")
    List<Teacher> findByCourse(@Param("id") Long id);

    @Query(value = "SELECT teacher FROM Teacher teacher JOIN teacher.courseTeachers ct JOIN ct.course c WHERE c.id = :id")
    Page<Teacher> findByCoursePage(@Param("id") Long id, Pageable pageable);

    @Query("SELECT t FROM Teacher t JOIN t.roles r WHERE r = :role ")
    List<Teacher> findByRole(@Param("role") String role);

    @Query("SELECT ct.correctedHours FROM Teacher t JOIN t.courseTeachers ct JOIN ct.course c " +
            "WHERE t.id=:idTeacher AND c.id=:idCourse")
    Integer getCorrectedHoursByIdTeacher(@Param("idTeacher") Long idTeacher, @Param("idCourse")Long idCourse);

    @Query("SELECT (SUM(pods.chosenHours)/ct.correctedHours)*100 as percentage, SUM(pods.chosenHours) as charge, " +
            "COUNT(subject.id) as numSubjects FROM Subject subject JOIN subject.pods pods JOIN pods.course c JOIN pods.teacher t  JOIN t.courseTeachers ct" +
            " WHERE c.id = :idCourse AND t.id = :idTeacher AND ct.course.id = :idCourse")
    Object statisticsByTeacherAndCourse(@Param("idTeacher") Long idTeacher, @Param("idCourse") Long idCourse);

    @Query("SELECT DISTINCT teacher.id, teacher.name, sT.name, ctT.correctedHours FROM Teacher teacher " +
            "INNER JOIN teacher.pods podsT INNER JOIN podsT.course cT INNER JOIN podsT.subject sT " +
            "INNER JOIN teacher.courseTeachers ctT WHERE sT.id in (SELECT DISTINCT subject.id FROM Subject subject JOIN subject.pods pods " +
            "JOIN pods.course c JOIN pods.teacher t WHERE c.id = :idCourse AND t.id = :idTeacher) AND cT.id = :idCourse " +
            "AND ctT.course.id = :idCourse AND teacher.id <> :idTeacher")
    List<Object[]> findMatesByTeacherAndCourse(@Param("idTeacher") Long idTeacher, @Param("idCourse") Long idCourse);

    @Query(value = "SELECT teacher, ct FROM Teacher teacher " +
            "JOIN teacher.courseTeachers ct JOIN ct.course c WHERE c.id = :idCourse ")
    Page<Object[]> allTeachersStatistics(@Param("idCourse") Long idCourse, Pageable pageable);

    @Query("SELECT SUM(pods.chosenHours) FROM Teacher teacher JOIN teacher.pods pods JOIN pods.course c JOIN pods.teacher t" +
            " WHERE c.id = :idCourse AND t.id = :idTeacher")
    Integer chosenHoursTeacher(@Param("idTeacher") Long idTeacher, @Param("idCourse") Long idCourse);

    @Query("SELECT DISTINCT SUM(pods.chosenHours) FROM Teacher teacher JOIN teacher.pods pods " +
            "JOIN pods.course c WHERE c.id = :idCourse")
    Integer getSumChosenHours(@Param("idCourse")Long idCourse);

    @Query("SELECT SUM(st.correctedHours) as totalForce FROM Teacher teacher JOIN teacher.courseTeachers st JOIN st.course c WHERE c.id = :idCourse")
    Integer getSumCorrectedHours(@Param("idCourse")Long idCourse);
}
