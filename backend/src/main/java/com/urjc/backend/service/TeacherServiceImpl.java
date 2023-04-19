package com.urjc.backend.service;

import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.CourseRepository;
import com.urjc.backend.repository.SubjectRepository;
import com.urjc.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
public class TeacherServiceImpl implements TeacherService{

    private static final String ADMIN = "ADMIN";

    @Value("${email.main.admin}")
    private String emailMainAdmin;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;


    @Override
    public Teacher findIfIsInCurrentCourse(String email){

        Teacher teacher = findByEmail(email);
        if(teacher == null) {
            return null;
        }

        if(teacher.getRoles().contains(ADMIN)){
            return teacher;
        }

        Optional<Course> course = courseRepository.findFirst1ByOrderByCreationDateDesc();

        if (course.isPresent() && course.get().isTeacherInCourse(teacher)) {
            return teacher;
        }
        return null;
    }

    @Override
    public Teacher findByEmail(String email){
        return teacherRepository.findByEmail(email);
    }

    private List<Teacher> findByCourse(Long idCourse){
        return teacherRepository.findByCourse(idCourse);
    }

    @Override
    public void deleteTeachersByCourse(Course course){
        List<Teacher> teachers = findByCourse(course.getId());
        for (Teacher teacher: teachers) {
            if(!teacher.getEmail().equals(emailMainAdmin) && teacher.getCourseTeachers().size() == 1){
                delete(teacher);
            }else {
                teacher.unjoinCourse(course);
                save(teacher);
            }
        }
    }

    @Override
    public Object[] getEditableData(String email, Course course){
        Teacher teacher = findByEmail(email);
        return teacherRepository.findEditableData(teacher.getId(), course.getId()).get(0);
    }

    @Override
    public void delete(Teacher teacher){
        teacherRepository.delete(teacher);
    }

    @Override
    public void updateAdminsInLastCourse(){
        Optional<Course> lastCourse = courseRepository.findFirst1ByOrderByCreationDateDesc();
        if(lastCourse.isPresent()) {
            List<Teacher> admins = teacherRepository.findByRole(ADMIN);

            for (Teacher teacher : admins) {
                if (!teacher.getEmail().equals(emailMainAdmin) && (!lastCourse.get().isTeacherInCourse(teacher))) {
                    teacher.getRoles().remove(ADMIN);
                    save(teacher);
                }
                if(teacher.getEmail().equals(emailMainAdmin) && (!lastCourse.get().isTeacherInCourse(teacher))){
                    lastCourse.get().addTeacher(teacher, 100);
                }
            }
        }
    }

    @Override
    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public List<Teacher> findAllByCourse(Long id, Pageable pageable) {
        return teacherRepository.findByCoursePage(id, pageable).getContent();
    }

    @Override
    public List<Object[]> allTeachersStatistics(Course course, Pageable pageable) {

        Page<Object[]> listDataTeachers = teacherRepository.findStatisticsByCourseAndPage(course.getId(), pageable);

        List<Object[]> resultList = new ArrayList<>();

        for (Object[] dataTeachers: listDataTeachers) {
            Integer[] personalStatistics = teacherRepository.statisticsByTeacherAndCourse(((Teacher) dataTeachers[0]).getId(), course.getId()).get(0);
            Object[] finalData = new Object[]{((Teacher) dataTeachers[0]).getName(), dataTeachers[1],
                    personalStatistics[0] != null ? personalStatistics[0] : 0,
                    personalStatistics[1] != null ? personalStatistics[1] : 0, personalStatistics[2] };
            resultList.add(finalData);
        }
        return resultList;
    }

    @Override
    public List<Teacher> findAllByRole(String role) {
        return teacherRepository.findByRole(role);
    }

    @Override
    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    @Override
    public Integer[] findPersonalStatistics(Long idTeacher, Course course){
        int numConflicts = 0;

        Integer[] statistics = teacherRepository.statisticsByTeacherAndCourse(idTeacher, course.getId()).get(0);

        statistics[0] = statistics[0] != null ? statistics[0] : 0;
        statistics[1] = statistics[1] != null ? statistics[1] : 0;

        for (Subject subject:subjectRepository.findByCourseAndTeacher(course.getId(), idTeacher, Sort.unsorted())) {
            List<String> teachers = subject.recordSubject().get(course.getName());
            Integer totalChosenHours = teachers.stream().map(item -> Integer.parseInt(item.substring(0, item.indexOf("h")))).reduce(0, Integer::sum);
            if(totalChosenHours > subject.getTotalHours() && subject.getTotalHours() != 0){
                numConflicts++;
            }
        }

        Object[] result = teacherRepository.findEditableData(idTeacher, course.getId()).get(0);
        Integer correctedHours = (Integer) result[0];

        return new Integer[]{ statistics[0], statistics[1], correctedHours, statistics[2], numConflicts };
    }

    @Override
    public List<Object[]> findMates(Long idTeacher, Long idCourse){
        List<Object[]> mates = teacherRepository.findMatesByTeacherAndCourse(idTeacher, idCourse);
        List<Object[]> result = new ArrayList<>();

        //get their totalChosenHours percentage
        for (Object[] mate: mates) {
            Integer chosenHoursTeacher = teacherRepository.findChosenHoursByTeacherAndCourse(((Long) mate[0]), idCourse);
            Object[] obj = new Object[]{mate[1], mate[2], chosenHoursTeacher * 100/ ((Integer) mate[3]) };
            result.add(obj);
        }
        return result;
    }

    private Teacher setEntryValuesToTeacher(String[] values){
        for (int i = 0; i < values.length - 1; i++) {
            if(values[i].isBlank()){
                values[i] = null;
            }
        }

        return new Teacher(values[0], values[1]);
    }

    @Override
    public void saveAll(InputStream inputStream, Course course) throws IOException{
        BufferedReader br;
        String line;
        br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("[\\[\\]<>'\"!=]", "");
            String[] values = line.split(";", -1);

            if(!line.isBlank() && !(values[0].equals("Nombre"))){

                if(values.length != 3){
                    throw new GlobalException(HttpStatus.BAD_REQUEST, "Faltan datos de un docente en la linea: " + line);
                }

                Teacher teacher = setEntryValuesToTeacher(values);
                teacher.validate(line);

                Teacher teacherResult = findByEmail(teacher.getEmail());
                if (teacherResult == null) {
                    course.addTeacher(teacher, Integer.valueOf(values[2]));
                    course.validate(line);
                    save(teacher);
                }else if(!course.isTeacherInCourse(teacherResult)){
                    course.addTeacher(teacherResult, Integer.valueOf(values[2]));
                    course.validate(line);
                    save(teacherResult);
                }
            }
        }
    }
}
