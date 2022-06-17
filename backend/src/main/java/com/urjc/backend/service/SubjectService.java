package com.urjc.backend.service;

import com.urjc.backend.model.*;
import com.urjc.backend.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Transactional
public class SubjectService {
    private static final Logger log = LoggerFactory.getLogger(SubjectService.class);

    @Autowired
    private SubjectRepository subjectRepository;

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void delete(Subject subject){
        subjectRepository.delete(subject);
    }

    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    public List<Object[]> findAllByCourse(Course course, Pageable pageable) {

        Page<Object[]> p3 = subjectRepository.getSubjectsByCourse(course.getId(), pageable);

        //generate result and get teachers joined to each subject of a specific year
        List<Object[]> finalList = new ArrayList<>();

        for (Object[] item:p3.getContent()) {
            List<String> teachers = ((Subject) item[0]).recordSubject().get(course.getName());
            Object[] obj = new Object[] { item[0], item[1], teachers };
            finalList.add(obj);
        }

        return finalList;
    }

    public List<Subject> findAllInCurrentCourse(Long id) {
        return subjectRepository.findAllByCourse(id);
    }

    public Object[] findMySubjectsByCourse(Long idTeacher, Course course, Sort typeSort) {
        return subjectRepository.findMySubjectsByCourse(idTeacher, course.getId(), typeSort);
    }

    public List<Object[]> graphHoursPerSubject(Teacher teacher, Course course, Sort typeSort) {
        return subjectRepository.hoursPerSubject(teacher.getId(), course.getId(), typeSort);
    }

    public List<Object[]> graphPercentageHoursSubjects(Teacher teacher, Course course, Sort typeSort) {
        Integer totalHours = subjectRepository.totalHoursMySubjects(teacher.getId(), course.getId());
        return subjectRepository.percentageHoursSubjects(teacher.getId(), course.getId(), totalHours,typeSort);
    }

    public List<Object[]> findMySubjects(Long idTeacher, Course course, Sort typeSort){
        List<Subject> mySubjects = subjectRepository.findAllMySubjects(idTeacher, course.getId(), typeSort);

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
            List<Object[]> finalList = new ArrayList<>();

            for (Subject subject : mySubjects) {
                List<String> conflicts = checkScheduleConflicts(subject, schedulesFromAllMySubjects);
                List<String> teachers = subject.recordSubject().get(course.getName());

                Integer totalChosenHours = teachers.stream().map(item -> Integer.parseInt(item.substring(0, item.indexOf("h")))).reduce(0, Integer::sum);

                Object[] obj = new Object[]{subject, subject.getTotalHours() - totalChosenHours, conflicts, teachers};
                finalList.add(obj);
            }

            return finalList;
        }

        return new ArrayList<>();
    }

    public List<String> checkScheduleConflicts(Subject subject, List<Object[]> allSchedulesFromMySubjects){
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
                        log.info(e.getMessage());
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
            return false;

        } catch (IOException e) {
            log.info(e.getMessage());
            return false;
        }
    }

    public void setNullValues(String[] values){
        for (int i = 0; i < 10; i++) {
            if(values[i] == ""){
                values[i] = null;
            }
        }
    }

    public Subject findSubjectIfExists(Subject subject){

        List<Subject> subjects = subjectRepository.existsSubject(subject);

        for (Subject storedSubject: subjects) {
            Boolean exists = isExact(subject, storedSubject);
            if(exists){
                return storedSubject;
            }

        }

        return null;
    }

    public Boolean isExact(Subject subject, Subject storedSubject){

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
        return subjectRepository.getTitles();
    }

    public List<String> getCampus() {
        return subjectRepository.getCampus();
    }

    public List<String> getTypes() {
        return subjectRepository.getTypes();
    }
}
