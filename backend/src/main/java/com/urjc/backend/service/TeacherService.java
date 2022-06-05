package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.CourseRepository;
import com.urjc.backend.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeacherService {
    private static final Logger log = LoggerFactory.getLogger(TeacherService.class);

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;


    public Teacher findIfIsInCurrentCourse(String email){

        Teacher teacher = findByEmail(email);
        List<Course> courses = courseRepository.findAllOrderByDate();

        if(teacher == null) {
            return null;
        }
        if(courses.isEmpty() || teacher.getRoles().contains("ADMIN")){
            return teacher;
        }
        Course course = courses.get(0);
        if (course.isTeacherInCourse(teacher)) {
            return teacher;
        }
        return null;
    }

    public Teacher findByEmail(String email){
        return teacherRepository.findByEmail(email);
    }

    public void deleteTeacher(Teacher teacher){
        teacherRepository.delete(teacher);
    }

    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public List<Teacher> getTeachersByPOD(Long id, Pageable pageable) {
        Page<Teacher> p = teacherRepository.getTeachersByPOD(id, pageable);
        return p.getContent();
    }

    public List<Teacher> getAllTeachersInCurrentCourse() {
        List<Course> courses = courseRepository.findAllOrderByDate();
        if(courses.size() != 0){
            Long idLastPod = courses.get(0).getId();
            return teacherRepository.getAllTeachersByPOD(idLastPod);
        }
        return null;
    }

    public List<Teacher> getTeachersByRole(String role) {
        return teacherRepository.findByRole(role);
    }

    public Optional<Teacher> findTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public void setNullValues(String[] values){
        for (int i = 0; i < values.length - 1; i++) {
            if(values[i] == ""){
                values[i] = null;
            }
        }
    }

    public Teacher getTeacherIfExists(Teacher teacher){
        return teacherRepository.existsTeacher(teacher);
    }

    public Boolean saveAllTeachers(MultipartFile file, Course course){
        BufferedReader br;
        try {

            if(!file.isEmpty()){
                String line;
                InputStream is = file.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    line = line.replaceAll("[\"=\'#!]", "");
                    String[] values = line.split(";", -1);

                    Teacher teacher;
                    setNullValues(values);

                    try {
                        teacher = new Teacher(values[0], values[1]);
                    }catch (Exception e){
                        log.info(e.getMessage());
                        return false;
                    }

                    Teacher teacherResult = getTeacherIfExists(teacher);
                    if (teacherResult == null) {
                        try{
                            course.addTeacher(teacher, Integer.valueOf(values[2]));
                            saveTeacher(teacher);
                        } catch (RuntimeException e){
                            log.info(e.getMessage());
                            return false;
                        }
                    }else{
                        try{
                            course.addTeacher(teacherResult, Integer.valueOf(values[2]));
                            saveTeacher(teacherResult);
                        } catch (RuntimeException e){
                            log.info(e.getMessage());
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;

        } catch (IOException e) {
            log.info(e.getMessage());
            return false;
        }
    }
}
