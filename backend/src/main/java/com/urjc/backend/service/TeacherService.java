package com.urjc.backend.service;

import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    public void createTeacher(Teacher teacher){
        teacherRepository.save(teacher);
    }

    //TODO elaborate query when teachers are stored
    public Teacher findByEmailCurrentCourse(String email){
        return teacherRepository.findByEmail(email);
    }
}
