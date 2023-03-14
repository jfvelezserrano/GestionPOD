package com.urjc.backend.controller;

import com.urjc.backend.Data;
import com.urjc.backend.dto.*;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.mapper.IStatisticsMapper;
import com.urjc.backend.mapper.IStatisticsMapperImpl;
import com.urjc.backend.mapper.ISubjectMapper;
import com.urjc.backend.mapper.ISubjectMapperImpl;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.CourseTeacher;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.CourseServiceImpl;
import com.urjc.backend.service.SubjectServiceImpl;
import com.urjc.backend.service.TeacherServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.urjc.backend.error.ErrorMessageConstants.NOT_FOUND_ID_COURSE;
import static com.urjc.backend.error.ErrorMessageConstants.NO_COURSE_YET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsRestControllerTest {

    @Mock
    SubjectServiceImpl subjectService;

    @Mock
    CourseServiceImpl courseService;

    @Mock
    TeacherServiceImpl teacherService;

    @InjectMocks
    StatisticsRestController statisticsRestController;

    IStatisticsMapper statisticsMapper = new IStatisticsMapperImpl();

    ISubjectMapper subjectMapper = new ISubjectMapperImpl();

    @Test
    void Should_ReturnPersonalStatistics_When_FindPersonalStatistics() {
        ReflectionTestUtils.setField(statisticsRestController, "statisticsMapper", statisticsMapper);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();

        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);
        when(teacherService.findPersonalStatistics(anyLong(), any())).thenReturn(new Integer[]{40, 40, 100, 2, 1});

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<StatisticsPersonalDTO> result = statisticsRestController.findPersonalStatistics();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(40, result.getBody().getPercentage()),
                () -> assertEquals(40, result.getBody().getCharge()),
                () -> assertEquals(100, result.getBody().getCorrectedHours()),
                () -> assertEquals(2, result.getBody().getNumSubjects()),
                () -> assertEquals(1, result.getBody().getNumConflicts()));

        verify(courseService).findLastCourse();
        verify(teacherService).findByEmail(anyString());
        verify(teacherService).findPersonalStatistics(anyLong(), any());
    }

    @Test
    void Should_ThrowException_When_FindPersonalStatistics() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            statisticsRestController.findPersonalStatistics();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(teacherService, never()).findPersonalStatistics(anyLong(), any());
    }

    @Test
    void Should_ReturnMates_When_FindMates() {
        ReflectionTestUtils.setField(statisticsRestController, "statisticsMapper", statisticsMapper);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();

        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);
        when(teacherService.findMates(anyLong(), anyLong())).thenReturn(Data.createListObject("Luis Rodriguez", "Estadística", 40));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<List<StatisticsMatesDTO>> result = statisticsRestController.findMates();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(StatisticsMatesDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals("Luis Rodriguez", result.getBody().get(0).getMateName()),
                () -> assertEquals("Estadística", result.getBody().get(0).getMateSubject()),
                () -> assertEquals(40, result.getBody().get(0).getMatePercentage()));

        verify(courseService).findLastCourse();
        verify(teacherService).findByEmail(anyString());
        verify(teacherService).findMates(anyLong(), anyLong());
    }

    @Test
    void Should_ThrowException_When_FindMates() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            statisticsRestController.findMates();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(teacherService, never()).findMates(anyLong(), anyLong());
    }

    @Test
    void Should_ReturnSubjects_When_FindMySubjectsByCourse() {
        ReflectionTestUtils.setField(statisticsRestController, "subjectMapper", subjectMapper);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();

        when(courseService.findById(anyLong())).thenReturn(Data.createCourse("2022-2023"));
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);
        when(subjectService.findByCourseAndTeacher(anyLong(), any(), any())).thenReturn(Data.createListSubject());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<List<SubjectDTO>> result = statisticsRestController.findMySubjectsByCourse(1l);

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(SubjectDTO.class, result.getBody().get(0).getClass()),
                () -> assertTrue(result.getBody().stream().anyMatch(s -> s.getName().equals("Multimedia") && s.getCode().equals("1223456"))));

        verify(courseService).findById(anyLong());
        verify(teacherService).findByEmail(anyString());
        verify(subjectService).findByCourseAndTeacher(anyLong(), any(), any());
    }

    @Test
    void Should_ThrowException_When_FindMySubjectsByCourse() {
        when(courseService.findById(anyLong())).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            statisticsRestController.findMySubjectsByCourse(1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NOT_FOUND_ID_COURSE + 1l, exception.getMessage()));

        verify(courseService).findById(anyLong());
        verify(teacherService, never()).findByEmail(anyString());
        verify(subjectService, never()).findByCourseAndTeacher(anyLong(), any(), any());
    }

    @Test
    void Should_ReturnStatistics_When_GraphHoursPerSubject() {
        ReflectionTestUtils.setField(statisticsRestController, "statisticsMapper", statisticsMapper);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();

        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);
        when(subjectService.hoursPerSubjectByTeacherAndCourse(any(), any(), any())).thenReturn(Data.createListObject("Estadística", 120, 40));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<List<StatisticsGraphHoursDTO>> result = statisticsRestController.graphHoursPerSubject();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(StatisticsGraphHoursDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals("Estadística", result.getBody().get(0).getSubjectName()),
                () -> assertEquals(120, result.getBody().get(0).getSubjectTotalHours()),
                () -> assertEquals(40, result.getBody().get(0).getTeacherChosenHours()));

        verify(courseService).findLastCourse();
        verify(teacherService).findByEmail(anyString());
        verify(subjectService).hoursPerSubjectByTeacherAndCourse(any(), any(), any());
    }

    @Test
    void Should_ThrowException_When_GraphHoursPerSubject() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            statisticsRestController.graphHoursPerSubject();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(subjectService, never()).hoursPerSubjectByTeacherAndCourse(any(), any(), any());
    }

    @Test
    void Should_ReturnStatistics_When_GraphPercentageHoursSubjects() {
        ReflectionTestUtils.setField(statisticsRestController, "statisticsMapper", statisticsMapper);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();

        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);
        when(subjectService.percentageHoursByTeacherAndCourse(any(), any(), any())).thenReturn(Data.createListObject("Estadística", 100));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<List<StatisticsGraphPercentageDTO>> result = statisticsRestController.graphPercentageHoursSubjects();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(StatisticsGraphPercentageDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals("Estadística", result.getBody().get(0).getSubjectName()),
                () -> assertEquals(100, result.getBody().get(0).getTeacherHoursPercentage()));

        verify(courseService).findLastCourse();
        verify(teacherService).findByEmail(anyString());
        verify(subjectService).percentageHoursByTeacherAndCourse(any(), any(), any());
    }

    @Test
    void Should_ThrowException_When_GraphPercentageHoursSubjects() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            statisticsRestController.graphPercentageHoursSubjects();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(subjectService, never()).percentageHoursByTeacherAndCourse(any(), any(), any());
    }

    @Test
    void Should_ReturnStatistics_When_GetTeachersStatistics() {
        ReflectionTestUtils.setField(statisticsRestController, "statisticsMapper", statisticsMapper);

        Optional<Course> course = Data.createCourse("2022-2023");
        when(courseService.findLastCourse()).thenReturn(course);
        Data.addCourseTeachers(Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get(), course.get());
        when(teacherService.allTeachersStatistics(any(), any()))
                .thenReturn(Data.createListObject("Luis Rodriguez", new CourseTeacher(), 40, 100, 2));

        ResponseEntity<List<StatisticsTeacherDTO>> result = statisticsRestController.getTeachersStatistics(0, "name");

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(StatisticsTeacherDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals("Luis Rodriguez", result.getBody().get(0).getName()),
                () -> assertEquals(40, result.getBody().get(0).getPercentage()),
                () -> assertEquals(2, result.getBody().get(0).getNumSubjects()),
                () -> assertEquals(100, result.getBody().get(0).getCharge()));

        verify(courseService).findLastCourse();
        verify(teacherService).allTeachersStatistics(any(), any());
    }

    @Test
    void Should_ThrowException_When_GetTeachersStatistics() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            statisticsRestController.getTeachersStatistics(0, "name");
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(teacherService, never()).allTeachersStatistics(any(), any());
    }

    @Test
    void Should_ReturnStatistics_When_GetGlobalStatistics() {
        ReflectionTestUtils.setField(statisticsRestController, "statisticsMapper", statisticsMapper);

        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(courseService.getGlobalStatistics(any())).thenReturn(new Integer[]{40, 400, 1000, 40, 1000, 50, 75, 150, 4});

        ResponseEntity<StatisticsGlobalDTO> result = statisticsRestController.getGlobalStatistics();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(StatisticsGlobalDTO.class, result.getBody().getClass()),
                () -> assertEquals(150, result.getBody().getNumSubjects()),
                () -> assertEquals(4, result.getBody().getNumConflicts()),
                () -> assertEquals(75, result.getBody().getNumCompletations()),
                () -> assertEquals(50, result.getBody().getPercentageCompletations()),
                () -> assertEquals(40, result.getBody().getPercentageCharge()),
                () -> assertEquals(40, result.getBody().getPercentageForce()),
                () -> assertEquals(1000, result.getBody().getTotalCharge()),
                () -> assertEquals(400, result.getBody().getTotalChosenHours()),
                () -> assertEquals(1000, result.getBody().getTotalCorrectHours()));

        verify(courseService).findLastCourse();
        verify(courseService).getGlobalStatistics(any());
    }

    @Test
    void Should_ThrowException_When_GetGlobalStatistics() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            statisticsRestController.getGlobalStatistics();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(courseService, never()).getGlobalStatistics(any());
    }
}
