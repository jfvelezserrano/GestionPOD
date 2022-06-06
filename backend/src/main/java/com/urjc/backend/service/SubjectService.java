package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
import com.urjc.backend.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;


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

    public List<Subject> findAllByPOD(Long id, Pageable pageable) {
        Page<Subject> p = subjectRepository.getSubjectsByPOD(id, pageable);
        return p.getContent();
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
