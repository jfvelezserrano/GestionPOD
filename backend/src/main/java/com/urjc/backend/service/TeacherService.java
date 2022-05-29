package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    private static final Logger log = LoggerFactory.getLogger(TeacherService.class);

    @Autowired
    private TeacherRepository teacherRepository;

    public void createTeacher(Teacher teacher){
        teacherRepository.save(teacher);
    }

    //TODO elaborate query when teachers are stored
    public Teacher findByEmailCurrentCourse(String email){
        return teacherRepository.findByEmail(email);
    }

    public Teacher saveTeacher(Teacher teacher) throws IOException {
        return teacherRepository.save(teacher);
    }

    public List<Teacher> getTeachersByPOD(Long id) {
        return teacherRepository.getTeachersByPOD(id);
    }

    public Optional<Teacher> findTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public Boolean saveAllTeachers(MultipartFile file, Course course){
        BufferedReader br;
        try {

            if(!file.isEmpty()){
                String line;
                InputStream is = file.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    line = line.replaceAll("[\"=\']", "");
                    String[] values = line.split(";", -1);

                    Teacher teacher;
                    setNullValues(values);
                    List<String> roles = new ArrayList<>();
                    roles.add("TEACHER");

                    try {
                        teacher = new Teacher(roles, values[0], values[1]);
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
}
