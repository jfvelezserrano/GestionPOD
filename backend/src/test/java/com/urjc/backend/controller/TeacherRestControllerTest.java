package com.urjc.backend.controller;

import com.urjc.backend.Data;
import com.urjc.backend.dto.*;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.error.exception.RedirectException;
import com.urjc.backend.mapper.*;
import com.urjc.backend.model.Course;
import com.urjc.backend.model.Subject;
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

import static com.urjc.backend.error.ErrorMessageConstants.NO_COURSE_YET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherRestControllerTest {

    @Mock
    SubjectServiceImpl subjectService;

    @Mock
    CourseServiceImpl courseService;

    @Mock
    TeacherServiceImpl teacherService;

    @InjectMocks
    TeacherRestController teacherRestController;

    ITeacherMapper teacherMapper = new ITeacherMapperImpl();

    ISubjectMapper subjectMapper = new ISubjectMapperImpl();

    ICourseMapper courseMapper = new ICourseMapperImpl();

    @Test
    void Should_AddAdminRole_When_UpdateRoles() {
        ReflectionTestUtils.setField(teacherRestController, "teacherMapper", teacherMapper);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);
        when(teacherService.findIfIsInCurrentCourse(anyString())).thenReturn(teacher);

        doAnswer(i -> {
            Teacher arg0 = i.getArgument(0);
            arg0.setId(1l);
            assertEquals("Luis Rodriguez", arg0.getName());
            return arg0;
        }).when(teacherService).save(any());

        ResponseEntity<TeacherDTO> result = teacherRestController.updateRole(teacherMapper
                .toTeacherDTO(Data.createAdmin("Luis Rodriguez", "ejemplo@ejemplo.com").get()));

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(TeacherDTO.class, result.getBody().getClass()),
                () -> assertEquals(2, result.getBody().getRoles().size()),
                () -> assertEquals("ejemplo@ejemplo.com", result.getBody().getEmail()));

        verify(teacherService).findByEmail(anyString());
        verify(teacherService).findIfIsInCurrentCourse(anyString());
        verify(teacherService).save(any());
    }

    @Test
    void Should_ThrowException_When_TeacherNotExists() {
        ReflectionTestUtils.setField(teacherRestController, "teacherMapper", teacherMapper);
        when(teacherService.findByEmail(anyString())).thenReturn(null);

        TeacherDTO teacherDTO = teacherMapper
                .toTeacherDTO(Data.createAdmin("Luis Rodriguez", "ejemplo@ejemplo.com").get());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            teacherRestController.updateRole(teacherDTO);
        });

        assertAll(() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()),
                () -> assertEquals("El docente que se quiere actualizar es incorrecto o no puede actualizarse", exception.getMessage()));

        verify(teacherService).findByEmail(anyString());
        verify(teacherService, never()).findIfIsInCurrentCourse(anyString());
        verify(teacherService, never()).save(any());
    }

    @Test
    void Should_ThrowException_When_TeacherNotFoundInCurrentCourse() {
        ReflectionTestUtils.setField(teacherRestController, "teacherMapper", teacherMapper);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);
        when(teacherService.findIfIsInCurrentCourse(anyString())).thenReturn(null);

        TeacherDTO teacherDTO = teacherMapper
                .toTeacherDTO(Data.createAdmin("Luis Rodriguez", "ejemplo@ejemplo.com").get());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            teacherRestController.updateRole(teacherDTO);
        });

        assertAll(() -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()),
                () -> assertEquals("El docente que se quiere actualizar es incorrecto o no puede actualizarse", exception.getMessage()));

        verify(teacherService).findByEmail(anyString());
        verify(teacherService).findIfIsInCurrentCourse(anyString());
        verify(teacherService, never()).save(any());
    }

    @Test
    void Should_ReturnTeachersByRole_When_FindAllByRole() {
        ReflectionTestUtils.setField(teacherRestController, "teacherMapper", teacherMapper);
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(teacherService.findAllByRole(anyString())).thenReturn(Data.createListTeacher());

        ResponseEntity<List<TeacherDTO>> result = teacherRestController.findAllByRole("TEACHER");

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(TeacherDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals(2, result.getBody().size()),
                () -> assertTrue(result.getBody().get(0).getRoles().contains("TEACHER")),
                () -> assertTrue(result.getBody().get(1).getRoles().contains("TEACHER")));

        verify(courseService).findLastCourse();
        verify(teacherService).findAllByRole(anyString());
        verify(teacherService, never()).findAllByCourse(anyLong(), any());
    }

    @Test
    void Should_ReturnTeachers_When_FindAllByRoleAndCourse() {
        ReflectionTestUtils.setField(teacherRestController, "teacherMapper", teacherMapper);
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        when(teacherService.findAllByCourse(anyLong(), any())).thenReturn(Data.createListTeacher());

        ResponseEntity<List<TeacherDTO>> result = teacherRestController.findAllByRole(null);

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(TeacherDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals(2, result.getBody().size()));

        verify(courseService).findLastCourse();
        verify(teacherService).findAllByCourse(anyLong(), any());
        verify(teacherService, never()).findAllByRole(anyString());
    }

    @Test
    void Should_JoinToASubject_When_RequestJoinSubject() {
        Optional<Subject> subject = Data.createSubject("247857274", "Estadistica");
        Optional<Course> course = Data.createCourse("2022-2023");
        course.get().addSubject(subject.get());
        when(subjectService.findById(anyLong())).thenReturn(subject);
        when(courseService.findLastCourse()).thenReturn(course);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        doAnswer(i -> {
            Teacher arg0 = i.getArgument(0);
            arg0.setId(1l);
            assertAll(() -> assertEquals("Luis Rodriguez", arg0.getName()),
                    () -> assertTrue(arg0.getPods().stream()
                            .anyMatch(p -> p.getCourse() == course.get() && p.getSubject() == subject.get() && p.getTeacher() == teacher)));
            return arg0;
        }).when(teacherService).save(any());

        TeacherJoinSubjectDTO teacherJoinSubjectDTO = new TeacherJoinSubjectDTO();
        teacherJoinSubjectDTO.setHours(40);
        teacherRestController.joinSubject(teacherJoinSubjectDTO, 1l);

        verify(subjectService).findById(anyLong());
        verify(courseService).findLastCourse();
        verify(teacherService).findByEmail(anyString());
        verify(teacherService).save(any());
    }

    @Test
    void Should_ThrowExceptionNoCourseYet_When_JoinSubject() {
        Optional<Subject> subject = Data.createSubject("247857274", "Estadistica");
        when(subjectService.findById(anyLong())).thenReturn(subject);
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        TeacherJoinSubjectDTO teacherJoinSubjectDTO = new TeacherJoinSubjectDTO();
        teacherJoinSubjectDTO.setHours(40);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            teacherRestController.joinSubject(teacherJoinSubjectDTO, 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(subjectService).findById(anyLong());
        verify(courseService).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(teacherService, never()).save(any());
    }

    @Test
    void Should_ThrowExceptionNotExistsSubject_When_JoinSubject() {
        when(subjectService.findById(anyLong())).thenReturn(Optional.empty());

        TeacherJoinSubjectDTO teacherJoinSubjectDTO = new TeacherJoinSubjectDTO();
        teacherJoinSubjectDTO.setHours(40);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            teacherRestController.joinSubject(teacherJoinSubjectDTO, 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("No existe esa asignatura", exception.getMessage()));

        verify(subjectService).findById(anyLong());
        verify(courseService, never()).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(teacherService, never()).save(any());
    }

    @Test
    void Should_UnjoinASubject_When_RequestUnjoinSubject() {
        Optional<Subject> subject = Data.createSubject("247857274", "Estadistica");
        Optional<Course> course = Data.createCourse("2022-2023");
        course.get().addSubject(subject.get());
        when(subjectService.findById(anyLong())).thenReturn(subject);
        when(courseService.findLastCourse()).thenReturn(course);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        doAnswer(i -> {
            Teacher arg0 = i.getArgument(0);
            arg0.setId(1l);
            assertAll(() -> assertEquals("Luis Rodriguez", arg0.getName()),
                    () -> assertTrue(arg0.getPods().stream()
                            .noneMatch(p -> p.getCourse() == course.get() && p.getSubject() == subject.get() && p.getTeacher() == teacher)));
            return arg0;
        }).when(teacherService).save(any());

        teacherRestController.unjoinSubject(1l);

        verify(subjectService).findById(anyLong());
        verify(courseService).findLastCourse();
        verify(teacherService).findByEmail(anyString());
        verify(teacherService).save(any());
    }

    @Test
    void Should_ThrowExceptionNoCourseYet_When_UnjoinSubject() {
        Optional<Subject> subject = Data.createSubject("247857274", "Estadistica");
        when(subjectService.findById(anyLong())).thenReturn(subject);
        when(courseService.findLastCourse()).thenReturn(Optional.empty());


        GlobalException exception = assertThrows(GlobalException.class, () -> {
            teacherRestController.unjoinSubject(1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(subjectService).findById(anyLong());
        verify(courseService).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(teacherService, never()).save(any());
    }

    @Test
    void Should_ThrowExceptionNotExistsSubject_When_UnjoinSubject() {
        when(subjectService.findById(anyLong())).thenReturn(Optional.empty());

        RedirectException exception = assertThrows(RedirectException.class, () -> {
            teacherRestController.unjoinSubject( 1l);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("No se ha encontrado la asignatura con id 1", exception.getMessage()));

        verify(subjectService).findById(anyLong());
        verify(courseService, never()).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(teacherService, never()).save(any());
    }

    @Test
    void Should_ThrowException_When_FindAllMySubjects() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            teacherRestController.findAllMySubjects("name");
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(subjectService, never()).findConflictsByTeacherAndCourse(anyLong(), any(), any());
    }

    @Test
    void Should_ReturnSubjectsWithConflicts_When_FindAllMySubjects() {
        ReflectionTestUtils.setField(teacherRestController, "subjectMapper", subjectMapper);
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);
        when(subjectService.findConflictsByTeacherAndCourse(anyLong(), any(), any()))
                .thenReturn(DataControllers.createSubjectsWithConflicts());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<List<SubjectTeacherDTO>> result = teacherRestController.findAllMySubjects("name");

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(SubjectTeacherDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals("30h Luis Rodriguez", result.getBody().get(0).getJoinedTeachers().get(0)),
                () -> assertTrue(result.getBody().get(0).getConflicts().isEmpty()),
                () -> assertEquals(1, result.getBody().size()));

        verify(courseService).findLastCourse();
        verify(teacherService).findByEmail(anyString());
        verify(subjectService).findConflictsByTeacherAndCourse(anyLong(), any(), any());
    }

    @Test
    void Should_ReturnCourses_When_FindAllMyCourses() {
        ReflectionTestUtils.setField(teacherRestController, "courseMapper", courseMapper);
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);
        when(courseService.findCoursesTakenByTeacher(anyLong())).thenReturn(Data.createListCourse("2022-2023", "2021-2022"));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<List<CourseDTO>> result = teacherRestController.findAllMyCourses();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(CourseDTO.class, result.getBody().get(0).getClass()),
                () -> assertEquals("2022-2023", result.getBody().get(0).getName()),
                () -> assertEquals("2021-2022", result.getBody().get(1).getName()),
                () -> assertEquals(2, result.getBody().size()));

        verify(courseService).findLastCourse();
        verify(teacherService).findByEmail(anyString());
        verify(courseService).findCoursesTakenByTeacher(anyLong());
    }

    @Test
    void Should_ThrowException_When_FindAllMyCourses() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            teacherRestController.findAllMyCourses();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(courseService, never()).findCoursesTakenByTeacher(anyLong());
    }

    @Test
    void Should_ReturnPersonalData_When_GetEditableData() {
        ReflectionTestUtils.setField(teacherRestController, "teacherMapper", teacherMapper);
        when(courseService.findLastCourse()).thenReturn(Data.createCourse("2022-2023"));
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        when(teacherService.getEditableData(anyString(), any())).thenReturn(Data.createListObject(120, null).get(0));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<CourseTeacherDTO> result = teacherRestController.getEditableData();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(CourseTeacherDTO.class, result.getBody().getClass()),
                () -> assertEquals(120, result.getBody().getCorrectedHours()),
                () -> assertNull(result.getBody().getObservation()));

        verify(courseService).findLastCourse();
        verify(teacherService).getEditableData(anyString(), any());
    }

    @Test
    void Should_ThrowException_When_GetEditableData() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            teacherRestController.getEditableData();
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(teacherService, never()).getEditableData(anyString(), any());
    }

    @Test
    void Should_EditData_When_RequestEditData() {
        Optional<Course> course = Data.createCourse("2022-2023");
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        Data.addCourseTeachers(teacher, course.get());

        when(courseService.findLastCourse()).thenReturn(course);
        when(teacherService.findByEmail(anyString())).thenReturn(teacher);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        doAnswer(i -> {
            Teacher arg0 = i.getArgument(0);
            arg0.setId(1l);
            assertTrue(arg0.getCourseTeachers().stream().anyMatch(ct -> ct.getCorrectedHours() == 120 && ct.getObservation().equals("prueba")));
            return arg0;
        }).when(teacherService).save(any());

        teacherRestController.editData(teacherMapper.toTeacherEditableDataDTO(120, "prueba"));

        verify(courseService).findLastCourse();
        verify(teacherService).findByEmail(anyString());
        verify(teacherService).save(any());
    }

    @Test
    void Should_ThrowException_When_EditData() {
        when(courseService.findLastCourse()).thenReturn(Optional.empty());

        CourseTeacherDTO courseTeacherDTO = teacherMapper.toTeacherEditableDataDTO(120, "prueba");

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            teacherRestController.editData(courseTeacherDTO);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals(NO_COURSE_YET, exception.getMessage()));

        verify(courseService).findLastCourse();
        verify(teacherService, never()).findByEmail(anyString());
        verify(teacherService, never()).save(any());
    }
}
