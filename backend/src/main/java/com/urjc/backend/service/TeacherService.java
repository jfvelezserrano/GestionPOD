package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Teacher;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    Teacher findIfIsInCurrentCourse(String email);
    Teacher findByEmail(String email);
    List<Teacher> findByCourse(Long idCourse);
    List<Teacher> findAllByCourse(Long id, Pageable pageable);
    List<Teacher> findAllByRole(String role);
    Optional<Teacher> findById(Long id);
    Integer[] findPersonalStatistics(Long idTeacher, Course course);
    List<Object[]> findMates(Long idTeacher, Long idCourse);

    Object[] getEditableData(String email, Course course);
    List<Object[]> allTeachersStatistics(Course course, Pageable pageable);
    Teacher getTeacherIfExists(Teacher teacher);

    void setNullValues(String[] values);

    void deleteTeachersNotInAnyCourse(Course course);
    void deleteAdmins();
    void delete(Teacher teacher);

    Teacher save(Teacher teacher);
    Boolean saveAll(MultipartFile file, Course course);
}
