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
public class CourseServiceImpl implements CourseService{

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectService subjectService;

    @Override
    public boolean exists(String courseName){ return courseRepository.findByName(courseName).isPresent(); }

    @Override
    public List<Course> findAllOrderByCreationDate(){
        return courseRepository.OrderByCreationDateDesc();
    }

    @Override
    public Optional<Course> findLastCourse(){
        return courseRepository.findFirst1ByOrderByCreationDateDesc();
    }

    @Override
    public Optional<Course> findById(Long id){
        return courseRepository.findById(id);
    }

    @Override
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void delete(Course course){
        courseRepository.delete(course);
        courseRepository.flush();
    }

    @Override
    public List<Course> findCoursesTakenByTeacher(Long idTeacher){
        return courseRepository.findCoursesTakenByTeacher(idTeacher);
    }

    @Override
    public Integer[] getGlobalStatistics(Course course){
        Integer[] totalHoursAndNumSubjects = subjectRepository.getSumTotalHoursAndSubjectsNumber(course.getId()).get(0);

        Integer totalCharge = totalHoursAndNumSubjects[0];
        Integer numSubjects = totalHoursAndNumSubjects[1];

        Integer totalCorrectHours = teacherRepository.findSumCorrectedHoursByCourse(course.getId());
        Integer totalChosenHours = teacherRepository.findSumChosenHoursByCourse(course.getId());

        totalCharge = totalCharge != null ? totalCharge : 0;
        totalChosenHours = totalChosenHours != null ? totalChosenHours : 0;

        Integer numConflicts = subjectService.searchByCourse(course, "Conflicto", "", null, "", "", Sort.unsorted()).size();
        Integer numCompletations = subjectService.searchByCourse(course, "Completa", "", null, "", "", Sort.unsorted()).size();

        return new Integer[]{ (totalCharge == 0 ? 0 : totalChosenHours * 100/totalCharge), totalChosenHours,
                totalCharge, (totalCorrectHours == 0 ? 0 : totalChosenHours * 100/totalCorrectHours), totalCorrectHours,
                (numSubjects == 0 ? 0 : numCompletations * 100/numSubjects), numCompletations, numSubjects, numConflicts};
    }

    @Override
    public ByteArrayInputStream writePODInCSV(List<String[]> body) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CSVPrinter printer = new CSVPrinter(new PrintWriter(stream), CSVFormat.Builder.create().setDelimiter(";").setHeader("\ufeff" + "Código", "Titulación",
                "Campus", "Curso", "Semestre", "Asignatura", "Tipo", "Horas", "Grupo", "Turno", "Horario",
                "Grupos asistencia", "Profesores").build());
        for (String[] line:body) {
            printer.printRecord(line);
        }

        printer.flush();
        return new ByteArrayInputStream(stream.toByteArray());
    }

    @Override
    public List<String[]> createContentForCSV(List<Object[]> subjectsAndTeachersCurrentCourse){

        List<String[]> content = new ArrayList<>();

        for (Object[] subjectAndTeacher:subjectsAndTeachersCurrentCourse) {
            Subject subject = ((Subject) subjectAndTeacher[0]);
            List<String> line = new ArrayList<>(List.of(subject.getCode(), subject.getTitle(), subject.getCampus(),
                    subject.getYear().toString(), subject.getQuarter(), subject.getName(), subject.getType(),
                    subject.getTotalHours().toString(), subject.getCareer(), subject.getTurn().toString()));

            int i = 0;
            StringBuilder itemSchedule = new StringBuilder();

            for (Schedule schedule:subject.getSchedules()) {
                itemSchedule.append(schedule.getDayWeek() + "(" + schedule.getStartTime() + " - " + schedule.getEndTime() + ")");
                i++;

                if (subject.getSchedules().size() != i) {
                    itemSchedule.append(", ");
                }
            }

            line.add(subject.getSchedules().size() != 0 ? itemSchedule.toString() : "");
            line.add(subject.getAssistanceCareers().size() != 0 ? Arrays.toString(subject.getAssistanceCareers().toArray()) : "");
            line.add(subjectAndTeacher[1] != null ? Arrays.toString(((List) subjectAndTeacher[1]).toArray()) : "");

            content.add(line.toArray(new String[13]));
        }

        return content;
    }
}
