package com.urjc.backend.repository;

import com.urjc.backend.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Teacher findByid(Long id);

    Teacher findByEmail(String email);


}
