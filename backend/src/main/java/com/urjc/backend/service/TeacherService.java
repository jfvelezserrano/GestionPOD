package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.CourseRepository;
import com.urjc.backend.repository.SubjectRepository;
import com.urjc.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;


    public Teacher findIfIsInCurrentCourse(String email){

        Teacher teacher = findByEmail(email);
        if(teacher == null) {
            return null;
        }

        if(teacher.getRoles().contains("ADMIN")){
            return teacher;
        }

        Optional<Course> course = courseRepository.findFirst1ByOrderByCreationDateDesc();

        if (course.isPresent() && course.get().isTeacherInCourse(teacher)) {
            return teacher;
        }
        return null;
    }

    public Teacher findByEmail(String email){
        return teacherRepository.findByEmail(email);
    }

    public void delete(Teacher teacher){
        teacherRepository.delete(teacher);
    }

    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public List<Teacher> findAllByCourse(Long id, Pageable pageable) {
        Page<Teacher> p = teacherRepository.findByCoursePage(id, pageable);
        return p.getContent();
    }

    public List<Teacher> findAllByRole(String role) {
        return teacherRepository.findByRole(role);
    }

    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    public Object[] findPersonalStatistics(Long idTeacher, Course course){
        int i = 0;

        Object[] statistics = ((Object[]) teacherRepository.statisticsByTeacherAndCourse(idTeacher, course.getId()));

        for (Subject subject:subjectRepository.findByTeacherAndCourse(idTeacher, course.getId(), Sort.unsorted())) {
            List<String> teachers = subject.recordSubject().get(course.getName());
            Integer totalChosenHours = teachers.stream().map(item -> Integer.parseInt(item.substring(0, item.indexOf("h")))).reduce(0, Integer::sum);
            if(totalChosenHours > subject.getTotalHours()){
                i++;
            }
        }

        if(statistics[0] == null){
            statistics[0] = 0;
        }
        if(statistics[1] == null){
            statistics[1] = 0;
        }

        Integer correctedHours = teacherRepository.findCorrectedHours(idTeacher);

        Object[] result = new Object[]{ statistics[0], statistics[1], correctedHours, statistics[2], i };

        return result;
    }

    public List<Object[]> findMates(Long idTeacher, Long idCourse){
        List<Object[]> mates = teacherRepository.findMatesByTeacherAndCourse(idTeacher, idCourse);
        List<Object[]> result = new ArrayList<>();

        //get their totalChosenHours percentage
        for (Object[] mate: mates) {
            Integer chosenHoursTeacher = teacherRepository.chosenHoursTeacher(((Long) mate[0]), idCourse);
            Object[] obj = new Object[]{mate[1], mate[2], chosenHoursTeacher * 100/ ((Integer) mate[3]) };
            result.add(obj);
        }
        return result;
    }

    public void setNullValues(String[] values){
        for (int i = 0; i < values.length - 1; i++) {
            if(values[i] == ""){
                values[i] = null;
            }
        }
    }

    public Teacher getTeacherIfExists(Teacher teacher){
        return teacherRepository.exists(teacher);
    }

    public Boolean saveAll(MultipartFile file, Course course){
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
                        return false;
                    }

                    Teacher teacherResult = getTeacherIfExists(teacher);
                    if (teacherResult == null) {
                        try{
                            course.addTeacher(teacher, Integer.valueOf(values[2]));
                            save(teacher);
                        } catch (RuntimeException e){
                            return false;
                        }
                    }else{
                        try{
                            course.addTeacher(teacherResult, Integer.valueOf(values[2]));
                            save(teacherResult);
                        } catch (RuntimeException e){
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;

        } catch (IOException e) {
            return false;
        }
    }
}
