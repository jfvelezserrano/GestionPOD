package com.urjc.backend.unitTest.restController;

import com.urjc.backend.controller.SubjectRestController;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.POD;
import com.urjc.backend.model.Subject;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.security.JWT;
import com.urjc.backend.service.CourseService;
import com.urjc.backend.service.SubjectService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;
import java.util.*;


@RunWith(MockitoJUnitRunner.class)
public class SubjectControllerTest {

    private Cookie cookie;

    @Autowired
    JWT jwt;

    @InjectMocks
    SubjectRestController subjectRestController;

    @Mock
    SubjectService subjectService;

    @Mock
    CourseService courseService;

    @BeforeEach
    public void addCookie(){
        String token = jwt.createJWT("a.merinom.2017@alumnos.urjc.es");
        this.cookie = new Cookie("token", token);
    }

    //TODO getTitles

    @Test
    public void givenTitle_WhenGetCurrentTitles_ThenReturnCurrentTitles(){
        Long id = 1L;
        String title = "Example title by course";
        String nameCourse = "2001-2002";

        Subject subject = new Subject();
        subject.setId(id);
        subject.setTitle(title);

        Optional<Course> course = Optional.of(new Course(nameCourse));
        course.get().setId(id);
        course.get().addSubject(subject);

        List<String> allTitles = new ArrayList<>();
        allTitles.add(title);

        Mockito.when(courseService.findLastCourse()).thenReturn(course);
        Mockito.when(subjectService.getTitlesByCourse(course.get().getId())).thenReturn(allTitles);

        ResponseEntity<List<String>> responseEntity = subjectRestController.getTitlesCurrentCourse();

        Assert.assertEquals(1, responseEntity.getBody().size());
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertEquals(title, responseEntity.getBody().get(0));
    }

    @Test
    public void givenEmptyCourse_WhenGetCurrentTitles_ThenReturnNotFound(){
        Mockito.when(courseService.findLastCourse()).thenReturn(Optional.empty());

        ResponseEntity<List<String>> responseEntity = subjectRestController.getTitlesCurrentCourse();

        Assert.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    //TODO getCampus

    //TODO getTypes

    @Test
    public void givenIdSubject_whenGetById_thenReturnSubjectAndTeachers(){
        Long id = Long.valueOf(1);
        String nameTeacher = "Juan López Cabrera";
        Integer hours = 12;
        String nameCourse = "2001-2002";

        Subject subject = new Subject();
        subject.setId(id);

        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setName(nameTeacher);

        List<String> teachers = new ArrayList<>();
        teachers.add(hours + "h " + nameTeacher);

        Optional<Course> course = Optional.of(new Course(nameCourse));
        course.get().setId(id);
        course.get().addSubject(subject);

        POD pod = new POD();
        pod.setTeacher(teacher);
        pod.setSubject(subject);
        pod.setCourse(course.get());
        pod.setChosenHours(hours);

        Set<POD> pods = new HashSet<>();
        pods.add(pod);

        subject.setPods(pods);

        Object[] objectToCompare = new Object[] { subject, teachers};

        Mockito.when(subjectService.findById(subject.getId())).thenReturn(Optional.of(subject));
        Mockito.when(courseService.findLastCourse()).thenReturn(course);

        //ResponseEntity<Object[]> responseEntity = subjectRestController.getByIdInCurrentCourse(subject.getId());

        //Assert.assertEquals(2, responseEntity.getBody().length);
        //Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        //Assert.assertEquals(objectToCompare[1], responseEntity.getBody()[1]);
    }

    @Test
    public void givenEmptyCourse_whenGetById_thenReturnNotFound(){
        Long id = 1L;

        Subject subject = new Subject();
        subject.setId(id);

        Mockito.when(subjectService.findById(subject.getId())).thenReturn(Optional.of(subject));
        Mockito.when(courseService.findLastCourse()).thenReturn(Optional.empty());

        //ResponseEntity<Object[]> responseEntity = subjectRestController.getByIdInCurrentCourse(subject.getId());

        //Assert.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void givenEmptySubject_whenGetById_thenReturnNotFound(){
        Long id = 1L;

        Subject subject = new Subject();
        subject.setId(id);

        Mockito.when(subjectService.findById(subject.getId())).thenReturn(Optional.empty());

        //ResponseEntity<Object[]> responseEntity = subjectRestController.getByIdInCurrentCourse(subject.getId());

        //Assert.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    //TODO findAllInCurrentCourse

    @Test
    public void givenIdSubject_whenGetRecord_thenReturnSubjectRecord() throws Exception {

        String nameTeacher = "Juan López Cabrera";
        Integer hours = 12;
        String nameCourse = "2001-2002";
        Long id = 1L;

        Subject subject = new Subject();
        subject.setId(id);

        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setName(nameTeacher);

        List<String> teachers = new ArrayList<>();
        teachers.add(hours + "h " + nameTeacher);

        Optional<Course> course = Optional.of(new Course(nameCourse));
        course.get().setId(id);
        course.get().addSubject(subject);

        POD pod = new POD();
        pod.setTeacher(teacher);
        pod.setSubject(subject);
        pod.setCourse(course.get());
        pod.setChosenHours(hours);

        Set<POD> pods = new HashSet<>();
        pods.add(pod);

        subject.setPods(pods);

        Map<String, List<String>> expectedResult = new HashMap<>();
        expectedResult.put(nameCourse, teachers);

        Mockito.when(subjectService.findById(subject.getId())).thenReturn(Optional.of(subject));

        ResponseEntity<Map<String, List<String>>> responseEntity = subjectRestController.recordSubject(subject.getId());

        Assert.assertEquals(1, responseEntity.getBody().keySet().size());
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertEquals(teachers, responseEntity.getBody().get(nameCourse));
    }

    @Test
    public void givenEmptySubject_whenGetRecord_thenReturnNotFound(){
        Long id = 1L;

        Subject subject = new Subject();
        subject.setId(id);

        Mockito.when(subjectService.findById(subject.getId())).thenReturn(Optional.empty());

        ResponseEntity<Map<String, List<String>>> responseEntity = subjectRestController.recordSubject(subject.getId());

        Assert.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void givenFilter_WhenSearchASubject_ThenReturnResults(){
        Subject subject = new Subject("Example title by course","Segundo Cuatrimestre","T");

        Course course = new Course("2001-2002");

        Object[] objectToCompare = new Object[] { subject, 12, null};
        List<Object[]> searchedResults = new ArrayList<>();
        searchedResults.add(objectToCompare);

        Mockito.when(courseService.findLastCourse()).thenReturn(Optional.of(course));
        Mockito.when(subjectService.searchByCourse(course, "", subject.getQuarter(),
                subject.getTurn(), subject.getTitle(), -1L, Sort.by("name").ascending())
        ).thenReturn(searchedResults);

        /*ResponseEntity<List<Object[]>> responseEntity = subjectRestController.search("", subject.getQuarter(),
                subject.getTurn(), subject.getTitle(), -1L, "name");

        Assert.assertNotNull(responseEntity.getBody());
        Assert.assertEquals(1, responseEntity.getBody().size());
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertEquals(subject, responseEntity.getBody().get(0)[0]);
        Assert.assertEquals(12, responseEntity.getBody().get(0)[1]);
        Assert.assertNull(responseEntity.getBody().get(0)[2]);*/
    }

    @Test
    public void givenEmptyCourse_WhenSearchASubject_ThenReturnNotFound(){
        String title = "Example title by course";
        String quarter = "Segundo Cuatrimestre";
        String turn = "T";
        String typeSort = "name";

        Mockito.when(courseService.findLastCourse()).thenReturn(Optional.empty());

        /*ResponseEntity<List<Object[]>> responseEntity = subjectRestController.search("", quarter, turn, title, -1L, typeSort);

        Assert.assertEquals(404, responseEntity.getStatusCodeValue());*/
    }
}
