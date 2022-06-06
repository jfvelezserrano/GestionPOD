package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRepository courseRepository;


    public Course create(String course){
        try {
            Course newCourse = new Course(course);
            return courseRepository.save(newCourse);
        }catch (Exception e){
            return null;
        }
    }

    public List<Course> findAll(){ return courseRepository.findAll(); }

    public Optional<Course> findById(Long id){ return courseRepository.findById(id); }

    public Course save(Optional<Course> course) {
        try {
            return courseRepository.save(course.get());
        }catch (Exception e){
            return null;
        }
    }

    public Boolean delete(Long id){
        Optional<Course> course = findById(id);
        if(course.isPresent()){
            courseRepository.delete(course.get());
            return true;
        }

        return false;
    }
}
