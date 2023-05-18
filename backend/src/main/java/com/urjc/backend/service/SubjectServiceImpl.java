package com.urjc.backend.service;

import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Schedule;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Service
@Transactional
public class SubjectServiceImpl implements SubjectService{

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    public void delete(Subject subject){ subjectRepository.delete(subject); }

    @Override
    public Optional<Subject> findById(Long id) { return subjectRepository.findById(id); }

    @Override
    public List<Object[]> findByCoursePage(Course course, Pageable pageable) {

        Page<Subject> subjects = subjectRepository.findByCoursePage(course.getId(), pageable);

        //generate result to return and get teachers joined to each subject of a specific course
        List<Object[]> resultList = new ArrayList<>();

        for (Subject subject:subjects.getContent()) {
            List<String> teachers = subject.recordSubject().get(course.getName());
            Object[] obj = new Object[] { subject, teachers };
            resultList.add(obj);
        }

        return resultList;
    }

    @Override
    public List<Object[]> searchByCourse(Course course, String occupation, String quarter, Character turn, String title,
                                         String emailTeacher, String subjectName, Sort sort) {

        if(quarter.equals("")){ quarter = null; }
        if(title.equals("")){ title = null; }
        if(emailTeacher.equals("")){ emailTeacher = null; }
        if(subjectName.equals("")){ subjectName = null; }

        List<Subject> subjectsSearched = subjectRepository.search(course.getId(), quarter, turn, title, emailTeacher, subjectName, sort);

        //generate result to return and get teachers joined to each subject of a specific course
        List<Object[]> resultList = new ArrayList<>();

        for (Subject subject:subjectsSearched) {
            List<String> teachers = subject.recordSubject().get(course.getName());

            Integer hoursLeft = getLeftHoursInSubject(teachers, subject);

            if(occupation.equals("") || (occupation.equals("Libre") && (hoursLeft > 0)) || (occupation.equals("Completa") && (hoursLeft == 0)) ||
                    (occupation.equals("Conflicto") && (hoursLeft < 0))){
                Object[] obj = new Object[] { subject, teachers, hoursLeft };
                resultList.add(obj);
            }
        }

        return resultList;
    }

    @Override
    public List<Subject> findByCourse(Long id) {
        return subjectRepository.findByCourse(id);
    }

    @Override
    public void deleteSubjectsByCourse(Course course){
        List<Subject> subjects = findByCourse(course.getId());
        for (Subject subject: subjects) {
            if(subject.getCourseSubjects().size() == 1){
                delete(subject);
            }else if(subject.getCourseSubjects().size() > 1){
                subject.unjoinCourse(course);
                save(subject);
            }
        }
    }

    @Override
    public List<Subject> findByCourseAndTeacher(Long idTeacher, Course course, Sort typeSort) {
        return subjectRepository.findByCourseAndTeacher(course.getId(), idTeacher, typeSort);
    }

    @Override
    public List<Object[]> hoursPerSubjectByTeacherAndCourse(Teacher teacher, Course course, Sort typeSort) {
        return subjectRepository.hoursPerSubjectByTeacherAndCourse(teacher.getId(), course.getId(), typeSort);
    }

    @Override
    public List<Object[]> percentageHoursByTeacherAndCourse(Teacher teacher, Course course, Sort typeSort) {
        Integer totalHours = subjectRepository.totalChosenHoursByTeacherAndCourse(teacher.getId(), course.getId());
        return subjectRepository.percentageHoursByTeacherAndCourse(teacher.getId(), course.getId(), totalHours, typeSort);
    }

    @Override
    public List<Object[]> findConflictsByTeacherAndCourse(Long idTeacher, Course course, Sort typeSort){
        List<Subject> mySubjects = subjectRepository.findByCourseAndTeacher(course.getId(), idTeacher, typeSort);

        if(!mySubjects.isEmpty()) {
            //get all schedules from my subjects
            List<Object[]> schedulesFromAllMySubjects = new ArrayList<>();

            for (Subject subject : mySubjects) {
                for (Schedule schedule : subject.getSchedules()) {
                    Object[] item = {subject.getCode(), schedule, subject.getQuarter(), subject.getName()};
                    schedulesFromAllMySubjects.add(item);
                }
            }

            //generate result and check if there is any conflict
            List<Object[]> resultList = new ArrayList<>();

            for (Subject subject : mySubjects) {
                List<String> conflicts = checkScheduleConflicts(subject, schedulesFromAllMySubjects);
                List<String> teachers = subject.recordSubject().get(course.getName());

                Integer leftHours = getLeftHoursInSubject(teachers, subject);

                Object[] obj = new Object[]{subject, teachers, leftHours, conflicts};
                resultList.add(obj);
            }

            return resultList;
        }

        return Collections.emptyList();
    }

    private Integer getLeftHoursInSubject(List<String> teachers, Subject subject){
        Integer totalChosenHours = 0;

        if(teachers != null) {
            totalChosenHours = teachers.stream().map(item -> Integer.parseInt(item.substring(0, item.indexOf("h")))).reduce(0, Integer::sum);
        }
        totalChosenHours = subject.getTotalHours() == 0 ? 0 : totalChosenHours;

        return subject.getTotalHours() - totalChosenHours;
    }

    private List<String> checkScheduleConflicts(Subject subject, List<Object[]> allSchedulesFromMySubjects){
        List<String> resultConflicts = new ArrayList<>();

        for (Schedule schedule: subject.getSchedules()) {
            for (Object[] item: allSchedulesFromMySubjects) {
                Schedule scheduleToCompare = ((Schedule) item[1]);
                if(!subject.getCode().equals(item[0]) && (subject.getQuarter().equals(item[2]))
                        && (schedule.getDayWeek().equals(scheduleToCompare.getDayWeek()))) {
                    String result = compareBothSchedules(schedule, scheduleToCompare);
                    if(!result.isEmpty()) { resultConflicts.add(item[3] + " - " + result); }
                }
            }
        }

        return resultConflicts;
    }

    private String compareBothSchedules(Schedule schedule, Schedule scheduleToCompare){
        String result = "";

        LocalTime scheduleStartTime = LocalTime.parse(schedule.getStartTime());
        LocalTime scheduleEndTime = LocalTime.parse(schedule.getEndTime());
        LocalTime scheduleToCompareStartTime = LocalTime.parse(scheduleToCompare.getStartTime());
        LocalTime scheduleToCompareEndTime = LocalTime.parse(scheduleToCompare.getEndTime());

        //time overlap
        if ((((scheduleStartTime.isBefore(scheduleToCompareStartTime)) || (scheduleStartTime.compareTo(scheduleToCompareStartTime) == 0))
                && scheduleToCompareStartTime.isBefore(scheduleEndTime)) ||
                (scheduleStartTime.isAfter(scheduleToCompareStartTime) && scheduleStartTime.isBefore(scheduleToCompareEndTime))) {
            result = "Solapamiento de horarios";
        }

        //nearby schedules
        else if ((scheduleStartTime.compareTo(scheduleToCompareEndTime) == 0) ||
                (Math.abs(scheduleStartTime.until(scheduleToCompareEndTime, ChronoUnit.MINUTES)) < 30) ||
                (Math.abs(scheduleEndTime.until(scheduleToCompareStartTime, ChronoUnit.MINUTES)) < 30)) {
            result = "Horarios cercanos";
        }

         return result;
    }

    @Override
    public void saveAll(InputStream inputStream, Course course) throws IOException{
        BufferedReader br;
        String line;
        br = new BufferedReader(new InputStreamReader(inputStream));

        while ((line = br.readLine()) != null) {
            line = line.replaceAll("[\\[\\]<>'\"!=]", "");
            String[] values = line.split(";", -1);

            if (!(line.isBlank()) && !(values[0].equals("Código"))) {

                if(values.length != 12 || values[3].isBlank() || values[7].isBlank()){
                    throw new GlobalException(HttpStatus.BAD_REQUEST, "Faltan datos de una asignatura en la linea: " + line);
                }

                try{
                    Integer.parseInt(values[3]);
                    Integer.parseInt(values[7]);
                } catch (NumberFormatException e) {
                    throw new GlobalException(HttpStatus.BAD_REQUEST, "Hay datos incorrectos y/o incompletos en la siguiente asignatura: " + line);
                }

                Subject subject = setEntryValuesToSubject(values);
                subject.validate(line);

                if(!isCodeInCourse(course.getId(), subject.getCode())) {
                    Subject subjectDDBB = findSubjectIfExists(subject);

                    if (subjectDDBB != null) {
                        course.addSubject(subjectDDBB);
                        save(subjectDDBB);
                    } else {
                        course.addSubject(subject);
                        save(subject);
                    }
                }
            }
        }
    }

    @Override
    public boolean isCodeInCourse(Long idCourse, String code) {
        return subjectRepository.findByCourseAndCode(idCourse, code) != null;
    }

    private Subject setEntryValuesToSubject(String[] values) {
        for (int i = 0; i < 10; i++) {
            if(values[i].isBlank()){
                values[i] = null;
            }
        }

        Subject subject = new Subject(values[0], values[5], values[1], Integer.parseInt(values[7]),
                values[2], Integer.parseInt(values[3]), values[4], values[6], values[9] == null ? null : values[9].charAt(0), values[8]);

        subject.setSchedulesByString(values[10]);
        subject.setAssistanceCareersByString(values[11]);

        return subject;
    }

    @Override
    public Subject findSubjectIfExists(Subject subject){
        List<Subject> subjects = subjectRepository.findSameValues(subject);

        for (Subject storedSubject: subjects) {
            boolean exists = isExact(subject, storedSubject);
            if(exists){
                return storedSubject;
            }
        }

        return null;
    }

    private boolean isExact(Subject subject, Subject storedSubject){

        boolean isEqual = storedSubject != null;

        if(isEqual) {
            isEqual = storedSubject.getAssistanceCareers().size() == subject.getAssistanceCareers().size();

            int i = 0;
            while(isEqual && i < storedSubject.getAssistanceCareers().size()){
                isEqual = subject.getAssistanceCareers().contains((storedSubject.getAssistanceCareers().get(i)));
                i++;
            }

            boolean isEqualSchedules = storedSubject.getSchedules().size() == subject.getSchedules().size();

            i = 0;
            while(isEqual && isEqualSchedules && i < storedSubject.getSchedules().size()){
                Schedule storedSchedule = storedSubject.getSchedules().get(i);
                isEqual = subject.getSchedules().stream().anyMatch(s -> s.getDayWeek().equals(storedSchedule.getDayWeek()) &&
                        s.getStartTime().equals(storedSchedule.getStartTime()) &&
                        s.getEndTime().equals(storedSchedule.getEndTime()));
                i++;
            }

            return isEqual && isEqualSchedules;

        }

        return false;
    }

    @Override
    public List<String> getTitles() {
        return subjectRepository.getTitles();
    }

    @Override
    public List<String> getTitlesByCourse(Long idCourse) {
        return subjectRepository.getTitlesByCourse(idCourse);
    }

    @Override
    public List<String> getSubjectsByCourse(Long idCourse) {
        return subjectRepository.getSubjectsByCourse(idCourse);
    }

    @Override
    public List<String> getCampus() {
        return subjectRepository.getCampus();
    }

    @Override
    public List<String> getTypes() {
        return subjectRepository.getTypes();
    }
}
