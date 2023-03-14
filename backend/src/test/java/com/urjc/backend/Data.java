package com.urjc.backend;

import com.urjc.backend.model.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Data {
    public static Optional<Course> createCourse(String nameCourse) {
        Optional<Course> course = Optional.of(new Course(nameCourse));
        course.get().setId(1l);
        course.get().setPods(new HashSet<>());
        course.get().addTeacher(createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get(), 100);
        return course;
    }

    public static List<Course> createListCourse(String nameCourse1, String nameCourse2) {
        Course course1 = createCourse(nameCourse1).get();
        Course course2 = createCourse(nameCourse2).get();

        return Arrays.asList(course1, course2);
    }

    public static List<String> createListStrings(String... args) {
        return Arrays.asList(args);
    }

    public static List<Integer[]> createListIntegers(Integer... args) {
        List<Integer[]> editableDataList = new ArrayList<>();
        editableDataList.add(args);
        return editableDataList;
    }

    public static Optional<Subject> createSubject(String code, String name){
        Optional<Subject> subject = Optional.of(new Subject(code, name, "(2034) Grado Ingeniería Software (M)", 200,
                "Móstoles", 3, "Segundo Cuatrimestre", "Obligatoria", 'M', "G_SOFT_2A(M)"));

        subject.get().setSchedules(new ArrayList<>());
        addSchedule(subject.get(), 'L', "11:00", "13:00");
        subject.get().setAssistanceCareers(new ArrayList<>());
        Set<POD> pods = new HashSet<>();
        pods.add(createPOD(subject.get()));
        subject.get().setPods(pods);
        return subject;
    }

    public static List<Subject> createListSubject() {
        Subject subject1 = createSubject("789456", "Estadística").get();
        Subject subject2 = createSubject("1223456", "Multimedia").get();

        addCourseSubjects(subject2, createCourse("2022-2023").get());
        addCourseSubjects(subject1, createCourse("2022-2023").get());
        addCourseSubjects(subject1, createCourse("2021-2022").get());

        addSchedule(subject1, 'X',"09:00", "11:00");

        return Arrays.asList(subject1, subject2);
    }

    private static POD createPOD(Subject subject){
        POD pod = new POD();
        pod.setSubject(subject);
        pod.setCourse(createCourse("2022-2023").get());
        pod.setTeacher(createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get());
        pod.setChosenHours(30);
        return pod;
    }

    private static void addSchedule(Subject subject, Character day, String startTime, String endTime){
        Schedule schedule = new Schedule(day, startTime, endTime);
        subject.getSchedules().add(schedule);
    }

    public static void addCourseSubjects(Subject subject, Course course){
        CourseSubject courseSubject = new CourseSubject();
        courseSubject.setSubject(subject);
        courseSubject.setCourse(course);

        course.getCourseSubjects().add(courseSubject);
        subject.getCourseSubjects().add(courseSubject);
    }

    public static void addCourseTeachers(Teacher teacher, Course course){
        CourseTeacher courseTeacher = new CourseTeacher();
        courseTeacher.setTeacher(teacher);
        courseTeacher.setCourse(course);
        courseTeacher.setCorrectedHours(90);
        courseTeacher.setOriginalHours(90);
        courseTeacher.setObservation(null);

        teacher.getCourseTeachers().add(courseTeacher);
        course.getCourseTeachers().add(courseTeacher);
    }

    public static List<Teacher> createListTeacher() {
        Teacher teacher1 = createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        Teacher teacher2 = createAdmin("Pedro López", "ejemplo2@ejemplo2.com").get();

        addCourseTeachers(teacher1,createCourse("2022-2023").get());
        addCourseTeachers(teacher2, createCourse("2022-2023").get());
        addCourseTeachers(teacher2, createCourse("2021-2022").get());

        return Arrays.asList(teacher1, teacher2);
    }

    public static Optional<Teacher> createTeacher(String name, String email){
        Optional<Teacher> teacher = Optional.of(new Teacher(name, email));
        teacher.get().setId(1l);
        return teacher;
    }

    public static Optional<Teacher> createAdmin(String name, String email){
        List<String> roles = new ArrayList<>(Arrays.asList("ADMIN", "TEACHER"));
        return Optional.of(new Teacher(roles, name, email));
    }

    public static List<Object[]> createListObject(Object... args){
        List<Object[]> editableDataList = new ArrayList<>();
        editableDataList.add(args);
        return editableDataList;
    }

    public static List<Object[]> createResultSearch() {
        Subject subject1 = Data.createSubject("2323232", "Estructuras de datos").get();
        subject1.setSchedules(new ArrayList<>());
        subject1.setAssistanceCareers(new ArrayList<>());
        Subject subject2 = Data.createSubject("4545454", "Bases de datos").get();
        subject2.setSchedules(new ArrayList<>());
        subject2.setAssistanceCareers(new ArrayList<>());

        ArrayList teachers = new ArrayList();
        teachers.add("30h Luis Rodriguez");

        ArrayList<Object[]> result = new ArrayList<>();
        result.add(new Object[] {subject1, teachers, 20});
        result.add(new Object[] {subject2, Collections.emptyList(), 40});

        return result;
    }

    public static InputStream createInputStreamSubject() throws IOException {
        List<String> content = new ArrayList<>();
        content.add("Código;Titulación;Campus;Curso;Semestre;Asignatura;Tipo;Horas;Grupo;Turno;Horario;Grupos asisten;Profesores\n");
        content.add("2241019G1;(2241) Grado Psicología (Ar);Aranjuez;1;Segundo Cuatrimestre;Herramientas Tecnológicas para el Ejercicio de la Psicología (Grupo 1);Formación Básica;5;G_PSICO_1A(Ar);M;J(11:00 - 12:00);\n");
        content.add("2061012;(2061) Grado Ingeniería Informática (O);On-line;2;Primer Cuatrimestre;Programación Orientada a Objetos;Obligatoria;0;G_INFonline_2A(O);M;;");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String line : content) {
            outputStream.write(line.getBytes());
        }

        byte[] bytes = outputStream.toByteArray();

        return new ByteArrayInputStream(bytes);
    }

    public static InputStream createInputStreamSubjectError() throws IOException {
        List<String> content = new ArrayList<>();
        content.add("Código;Titulación;Campus;Curso;Semestre;Asignatura;Tipo;Horas;Grupo;Turno;Horario;Grupos asisten;Profesores\n");
        content.add("2241019G1;(2241) Grado Psicología (Ar);;1;Segundo Cuatrimestre;Herramientas Tecnológicas para el Ejercicio de la Psicología (Grupo 1);Formación Básica;5;G_PSICO_1A(Ar);M;J(11:00 - 12:00);;");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String line : content) {
            outputStream.write(line.getBytes());
        }

        byte[] bytes = outputStream.toByteArray();

        return new ByteArrayInputStream(bytes);
    }

    public static InputStream createInputStreamTeacher() throws IOException {
        List<String> content = new ArrayList<>();
        content.add("Luis Rodriguez;ejemplo@ejemplo.com;74\n");
        content.add("Castillo Muñoz, Facundo;facundo@ejemplo.com;90\n");
        content.add("Benítez Soto, Camilo;camilo@ejemplo.com;120");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String line : content) {
            outputStream.write(line.getBytes());
        }

        byte[] bytes = outputStream.toByteArray();

        return new ByteArrayInputStream(bytes);
    }

    public static InputStream createInputStreamTeacherError() throws IOException {
        List<String> content = new ArrayList<>();
        content.add("Luis Rodriguez;;74\n");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String line : content) {
            outputStream.write(line.getBytes());
        }

        byte[] bytes = outputStream.toByteArray();

        return new ByteArrayInputStream(bytes);
    }

    public static InputStream createEmptyInputStream(){
        return new ByteArrayInputStream(("").getBytes());
    }

    public static List<String[]> createBodyForCSV() {
        List<String[]> content = new ArrayList<>();

        List<String> body = new ArrayList<>(List.of("4564654", "(2034) Grado Ingeniería Software (M)", "Móstoles",
                "4", "Segundo Cuatrimestre", "Estructuras de Datos", "Obligatoria",
                "100", "G_SOFT_2A(M)", "M", "M(09:00 - 11:00), X(11:00 - 13:00)", "[G_SOFT_2A(M)]"));

        content.add(body.toArray(new String[13]));

        return content;
    }
}
