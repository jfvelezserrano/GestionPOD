package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Schedule;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.SubjectRepository;
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void delete(Subject subject){ subjectRepository.delete(subject); }

    public Optional<Subject> findById(Long id) { return subjectRepository.findById(id); }

    public List<Object[]> findByCoursePage(Course course, Pageable pageable) {

        Page<Subject> p3 = subjectRepository.findByCoursePage(course.getId(), pageable);

        //generate result to return and get teachers joined to each subject of a specific course
        List<Object[]> resultList = new ArrayList<>();

        for (Subject subject:p3.getContent()) {
            List<String> teachers = subject.recordSubject().get(course.getName());
            Object[] obj = new Object[] { subject, teachers };
            resultList.add(obj);
        }

        return resultList;
    }

    public List<Object[]> searchByCourse(Course course, String occupation, String quarter, String turn, String title, Long teacher, Sort sort) {

        if(quarter.equals("")){ quarter = null; }
        if(turn.equals("")){ turn = null; }
        if(title.equals("")){ title = null; }
        if(teacher == -1){ teacher = null; }

        List<Subject> subjectsSearched = subjectRepository.search(course.getId(), quarter, turn, title, teacher, sort);

        //generate result to return and get teachers joined to each subject of a specific course
        List<Object[]> resultList = new ArrayList<>();

        for (Subject subject:subjectsSearched) {
            List<String> teachers = subject.recordSubject().get(course.getName());

            Integer totalChosenHours = 0;
            if(teachers != null){
                totalChosenHours = teachers.stream().map(item -> Integer.parseInt(item.substring(0, item.indexOf("h")))).reduce(0, Integer::sum);
            }

            Integer hoursLeft = subject.getTotalHours() - totalChosenHours;
            if(occupation.equals("") || (occupation.equals("Libre") && (hoursLeft > 0)) || (occupation.equals("Completa") && (hoursLeft == 0)) ||
                    (occupation.equals("Conflicto") && (hoursLeft < 0))){
                Object[] obj = new Object[] { subject, hoursLeft, teachers };
                resultList.add(obj);
            }
        }

        return resultList;
    }

    public List<Subject> findByCourse(Long id) {
        return subjectRepository.findByCourse(id);
    }

    public List<Object[]> findNameAndQuarterByTeacherAndCourse(Long idTeacher, Course course, Sort typeSort) {
        return subjectRepository.findNameAndQuarterByTeacherAndCourse(idTeacher, course.getId(), typeSort);
    }

    public List<Object[]> hoursPerSubjectByTeacherAndCourse(Teacher teacher, Course course, Sort typeSort) {
        return subjectRepository.hoursPerSubjectByTeacherAndCourse(teacher.getId(), course.getId(), typeSort);
    }

    public List<Object[]> percentageHoursByTeacherAndCourse(Teacher teacher, Course course, Sort typeSort) {
        Integer totalHours = subjectRepository.totalChosenHoursByTeacherAndCourse(teacher.getId(), course.getId());
        return subjectRepository.percentageHoursByTeacherAndCourse(teacher.getId(), course.getId(), totalHours,typeSort);
    }

    public List<Object[]> findByTeacherAndCourse(Long idTeacher, Course course, Sort typeSort){
        List<Subject> mySubjects = subjectRepository.findByTeacherAndCourse(idTeacher, course.getId(), typeSort);

        if(mySubjects.size() != 0) {
            //get all schedules from my subjects
            List<Object[]> schedulesFromAllMySubjects = new ArrayList<>();

            for (Subject subject : mySubjects) {
                for (Schedule schedule : subject.getSchedules()) {
                    Object[] item = {subject.getName(), schedule};
                    schedulesFromAllMySubjects.add(item);
                }
            }

            //generate result and check if there is any conflict
            List<Object[]> resultList = new ArrayList<>();

            for (Subject subject : mySubjects) {
                List<String> conflicts = checkScheduleConflicts(subject, schedulesFromAllMySubjects);
                List<String> teachers = subject.recordSubject().get(course.getName());

                Integer totalChosenHours = teachers.stream().map(item -> Integer.parseInt(item.substring(0, item.indexOf("h")))).reduce(0, Integer::sum);

                Object[] obj = new Object[]{subject, subject.getTotalHours() - totalChosenHours, conflicts, teachers};
                resultList.add(obj);
            }

            return resultList;
        }

        return new ArrayList<>();
    }

    private List<String> checkScheduleConflicts(Subject subject, List<Object[]> allSchedulesFromMySubjects){
        List<String> resultConflicts = new ArrayList<>();

        for (Schedule schedule: subject.getSchedules()) {
            for (Object[] item: allSchedulesFromMySubjects) {
                if(subject.getName() != item[0]) {
                    Schedule scheduleToCompare = ((Schedule) item[1]);

                    if ((schedule.getDayWeek().equals(scheduleToCompare.getDayWeek()))) {

                        LocalTime scheduleStartTime = LocalTime.parse(schedule.getStartTime());
                        LocalTime scheduleEndTime = LocalTime.parse(schedule.getEndTime());
                        LocalTime scheduleToCompareStartTime = LocalTime.parse(scheduleToCompare.getStartTime());
                        LocalTime scheduleToCompareEndTime = LocalTime.parse(scheduleToCompare.getEndTime());

                        //time overlap
                        if ((( (scheduleStartTime.isBefore(scheduleToCompareStartTime)) || (scheduleStartTime.compareTo(scheduleToCompareStartTime) == 0) )
                                && scheduleToCompareStartTime.isBefore(scheduleEndTime)) ||
                                (scheduleStartTime.isAfter(scheduleToCompareStartTime) && scheduleStartTime.isBefore(scheduleToCompareEndTime))) {
                            String result = item[0] + " - Solapamiento de horarios";
                            resultConflicts.add(result);
                        }

                        //nearby schedules
                        else if ((scheduleStartTime.compareTo(scheduleToCompareEndTime) == 0) ||
                                (Math.abs(scheduleStartTime.until(scheduleToCompareEndTime, ChronoUnit.MINUTES)) < 30) ||
                                (Math.abs(scheduleEndTime.until(scheduleToCompareStartTime, ChronoUnit.MINUTES)) < 30)) {
                            String result = item[0] + " - Horarios cercanos";
                            resultConflicts.add(result);
                        }
                    }
                }
            }
        }

        return resultConflicts;
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

                if (!(values[0].equals("Codigo")) && !(values[0].equals("Código"))) {

                    Subject subject;

                    setNullValues(values);

                    try {
                        subject = new Subject(values[0], values[5], values[1], Integer.parseInt(values[7]),
                                values[2], Integer.parseInt(values[3]), values[4], values[6], values[9], values[8]);
                    } catch (Exception e) {
                        return false;
                    }
                    if (!values[10].equals("")) {
                        subject.setSchedulesByString(values[10]);
                    }
                    if (!values[11].equals("")) {
                        subject.setAssistanceCareersByString(values[11]);
                    }

                    Subject newSubject = findSubjectIfExists(subject);
                    if (newSubject != null) {
                        course.addSubject(newSubject);
                        save(newSubject);
                    } else {
                        course.addSubject(subject);
                        save(subject);
                    }
                }
            }
                return true;
            }
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    private void setNullValues(String[] values){
        for (int i = 0; i < 10; i++) {
            if(values[i] == ""){
                values[i] = null;
            }
        }
    }

    public Subject findSubjectIfExists(Subject subject){

        List<Subject> subjects = subjectRepository.sameValues(subject);

        for (Subject storedSubject: subjects) {
            Boolean exists = isExact(subject, storedSubject);
            if(exists){
                return storedSubject;
            }

        }

        return null;
    }

    private Boolean isExact(Subject subject, Subject storedSubject){

        Boolean isEqual = storedSubject != null;

        if(isEqual) {
            isEqual = ((storedSubject.getAssitanceCareers().size() == 0) && (subject.getAssitanceCareers() == null)) ||
                    storedSubject.getAssitanceCareers().size() == subject.getAssitanceCareers().size();

            if(isEqual && storedSubject.getAssitanceCareers().size() != 0) {
                for (int i = 0; i < storedSubject.getAssitanceCareers().size(); i++) {
                    isEqual = subject.getAssitanceCareers().get(i).equals(storedSubject.getAssitanceCareers().get(i));
                    if (!isEqual) {
                        break;
                    }
                }
            }

            Boolean isEqualSchedules = ((storedSubject.getSchedules().size() == 0) && (subject.getSchedules() == null)) ||
                    storedSubject.getSchedules().size() == subject.getSchedules().size();

            if(isEqual && isEqualSchedules && storedSubject.getSchedules().size() != 0) {
                for (int i = 0; i < storedSubject.getSchedules().size(); i++) {
                    isEqual = subject.getSchedules().get(i).getDayWeek().equals(storedSubject.getSchedules().get(i).getDayWeek()) &&
                            subject.getSchedules().get(i).getStartTime().equals(storedSubject.getSchedules().get(i).getStartTime()) &&
                            subject.getSchedules().get(i).getEndTime().equals(storedSubject.getSchedules().get(i).getEndTime());
                    if (!isEqual) {
                        break;
                    }
                }
            }
        }

        if(isEqual){
            return true;
        }
        return false;
    }

    public List<String> getTitles() {
        List<String> a = subjectRepository.getTitles();
        return a;
    }

    public List<String> getTitlesByCourse(Long idCourse) {
        return subjectRepository.getTitlesByCourse(idCourse);
    }

    public List<String> getCampus() {
        return subjectRepository.getCampus();
    }

    public List<String> getTypes() {
        return subjectRepository.getTypes();
    }
}
