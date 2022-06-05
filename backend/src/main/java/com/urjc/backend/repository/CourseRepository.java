package com.urjc.backend.repository;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findById(Long id);

    @Query("SELECT c FROM Course c ORDER BY c.creationDate DESC")
    List<Course> findAllOrderByDate();
}
