package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;


    public Boolean exists(String courseName){ return courseRepository.findByName(courseName) != null; }

    public List<Course> findAllOrderByCreationDate(){
        return courseRepository.OrderByCreationDateDesc();
    }

    public Optional<Course> findLastCourse(){
        return courseRepository.findFirst1ByOrderByCreationDateDesc();
    }

    public Optional<Course> findById(Long id){
        return courseRepository.findById(id);
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public void deleteById(Long id){ courseRepository.deleteById(id); }

    public List<Course> findByTeacherOrderByCreationDate(Long idTeacher){
        return courseRepository.findByTeacherOrderByCreationDateDesc(idTeacher);
    }
}
