package com.urjc.backend.service;

import com.urjc.backend.model.Course;
import com.urjc.backend.model.Schedule;
import com.urjc.backend.model.Subject;
import com.urjc.backend.repository.CourseRepository;
import com.urjc.backend.repository.SubjectRepository;
import com.urjc.backend.repository.TeacherRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private static final CSVFormat FORMAT = CSVFormat.EXCEL.withDelimiter(';').withHeader("\ufeff" + "Codigo", "Titulación",
            "Campus", "Curso", "Semestre", "Asignatura", "Tipo", "Horas", "Grupo", "Turno", "Horario",
            "Grupos asisten", "Profesores");

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectService subjectService;


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

    public void delete(Course course){
            courseRepository.delete(course);
            courseRepository.flush();
    }

    public List<Course> findByTeacherOrderByCreationDate(Long idTeacher){
        return courseRepository.findByTeacherOrderByCreationDateDesc(idTeacher);
    }

    public Object[] getGlobalStatistics(Course course){
        Object[] totalHoursAndNumSubjects = ((Object[]) subjectRepository.getSumTotalHoursAndSubjectsNumber(course.getId()));
        Integer totalCorrectHours = teacherRepository.getSumCorrectedHours(course.getId());
        Integer totalChosenHours = teacherRepository.getSumChosenHours(course.getId());

        Integer numConflicts = subjectService.searchByCourse(course, "Conflicto", "", "", "", -1L, Sort.unsorted()).size();
        Integer numCompletations = subjectService.searchByCourse(course, "Completa", "", "", "", -1L, Sort.unsorted()).size();

        return new Object[]{ (totalChosenHours * 100/Integer.parseInt(String.valueOf(totalHoursAndNumSubjects[0]))), totalChosenHours,
                totalHoursAndNumSubjects[0], (totalChosenHours * 100/totalCorrectHours), totalCorrectHours,
                (numCompletations * 100/Integer.parseInt(String.valueOf(totalHoursAndNumSubjects[1]))), numCompletations,
                totalHoursAndNumSubjects[1], numConflicts};
    }

    public ByteArrayInputStream writePODInCSV(List<String[]> body){
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             CSVPrinter printer = new CSVPrinter(new PrintWriter(stream), FORMAT)) {
            for (String[] line:body) {
                printer.printRecord(line);
            }

            printer.flush();
            printer.close();
            return new ByteArrayInputStream(stream.toByteArray());
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<String[]> createContentForCSV(List<Object[]> subjectsAndTeachersCurrentCourse){

        List<String[]> content = new ArrayList<>();

        for (Object[] subjectAndTeacher:subjectsAndTeachersCurrentCourse) {
            Subject subject = ((Subject) subjectAndTeacher[0]);
            List<String> line = new ArrayList<>() {{ add(subject.getCode()); add(subject.getTitle());
                add(subject.getCampus()); add(subject.getYear().toString()); add(subject.getQuarter()); add(subject.getName());
                add(subject.getType()); add(subject.getTotalHours().toString()); add(subject.getCareer()); add(subject.getTurn()); }};

            int i = 0;
            String itemSchedule = "";

            if(subject.getSchedules().size() != 0){
                for (Schedule schedule:subject.getSchedules()) {
                    itemSchedule += schedule.getDayWeek() + "(" + schedule.getStartTime() + " - " + schedule.getEndTime() + ")";
                    i++;

                    if(subject.getSchedules().size() != i){
                        itemSchedule += ", ";
                    } else{
                        line.add(itemSchedule);
                    }
                }
            }else{
                line.add("");
            }

            if(subject.getAssitanceCareers().size() != 0){
                line.add(Arrays.toString(subject.getAssitanceCareers().toArray()));
            }else {
                line.add("");
            }

            if(subjectAndTeacher[2] != null){
                line.add(Arrays.toString(((List) subjectAndTeacher[2]).toArray()));
            }else {
                line.add("");
            }

            content.add(line.toArray(new String[13]));
        }

        return content;
    }
}
