package com.urjc.backend.controller;

import com.urjc.backend.Data;
import com.urjc.backend.dto.EmailRequestDTO;
import com.urjc.backend.dto.TeacherDTO;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.mapper.ITeacherMapper;
import com.urjc.backend.mapper.ITeacherMapperImpl;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.security.AuthenticateProvider;
import com.urjc.backend.security.JWT;
import com.urjc.backend.service.MailBoxServiceImpl;
import com.urjc.backend.service.TeacherServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginRestControllerTest {

    private final String emailMainAdmin = "a.merinom.2017@alumnos.urjc.es";

    @Mock
    TeacherServiceImpl teacherService;

    @Mock
    MailBoxServiceImpl mailBoxService;

    @InjectMocks
    LoginRestController loginRestController;

    ITeacherMapper teacherMapper = new ITeacherMapperImpl();

    @Mock
    JWT jwt;

    @Mock
    private AuthenticateProvider authenticationManager;

    @Test
    void Should_ThrowException_When_SendEmailAndNotExists() throws MessagingException {
        when(teacherService.findIfIsInCurrentCourse(anyString())).thenReturn(null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setEmail(emailMainAdmin);


        GlobalException exception = assertThrows(GlobalException.class, () -> {
            loginRestController.sendEmailLogin(emailRequestDTO, request);
        });

        assertAll(() -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("La dirección de correo es incorrecta", exception.getMessage()));

        verify(teacherService).findIfIsInCurrentCourse(anyString());
        verify(mailBoxService, never()).generateCodeEmail();
        verify(mailBoxService, never()).addCode(anyString(), anyString(), anyString());
        verify(mailBoxService, never()).sendEmail(anyString(), any());
    }

    @Test
    void Should_SendEmail_When_RequestSendEmail() throws MessagingException {
        when(teacherService.findIfIsInCurrentCourse(anyString())).thenReturn(Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get());
        when(mailBoxService.generateCodeEmail()).thenReturn("b6d6da5c-6a13-4200-b5ad-4e7901d37310--1320354403");
        doNothing().when(mailBoxService).addCode(anyString(), anyString(), anyString());
        doNothing().when(mailBoxService).sendEmail(anyString(), any());

        MockHttpServletRequest request = new MockHttpServletRequest();
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setEmail(emailMainAdmin);

        loginRestController.sendEmailLogin(emailRequestDTO, request);

        verify(teacherService).findIfIsInCurrentCourse(anyString());
        verify(mailBoxService).generateCodeEmail();
        verify(mailBoxService).addCode(anyString(), anyString(), anyString());
        verify(mailBoxService).sendEmail(anyString(), any());
    }

    @Test
    void Should_ThrowException_When_SendingEmailFails() throws MessagingException {
        when(teacherService.findIfIsInCurrentCourse(anyString())).thenReturn(Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get());
        when(mailBoxService.generateCodeEmail()).thenReturn("b6d6da5c-6a13-4200-b5ad-4e7901d37310--1320354403");
        doNothing().when(mailBoxService).addCode(anyString(), anyString(), anyString());
        doThrow(MessagingException.class).when(mailBoxService).sendEmail(anyString(), any());

        MockHttpServletRequest request = new MockHttpServletRequest();
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setEmail(emailMainAdmin);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            loginRestController.sendEmailLogin(emailRequestDTO, request);
        });

        assertAll(() -> assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus()),
                () -> assertEquals("Algo ha fallado. Revise que la dirección de correo es correcta.", exception.getMessage()));

        verify(teacherService).findIfIsInCurrentCourse(anyString());
        verify(mailBoxService).generateCodeEmail();
        verify(mailBoxService).addCode(anyString(), anyString(), anyString());
        verify(mailBoxService).sendEmail(anyString(), any());
    }

    @Test
    void Should_ThrowException_When_VerifyFails(){
        when(mailBoxService.isCorrect(anyString(), anyString())).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            loginRestController.verify("b6d6da5c-6a13-4200-b5ad-4e7901d37310--1320354403", request, response);
        });

        assertAll(() -> assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus()),
                () -> assertEquals("Acceso denegado", exception.getMessage()));

        verify(mailBoxService, never()).getEmailByCode(anyString());
    }

    @Test
    void Should_ReturnTeacher_When_Verify(){
        ReflectionTestUtils.setField(loginRestController, "teacherMapper", teacherMapper);

        when(mailBoxService.isCorrect(anyString(), anyString())).thenReturn(true);
        when(mailBoxService.getEmailByCode(anyString())).thenReturn("ejemplo@ejemplo.com");
        when(teacherService.findByEmail(anyString())).thenReturn(Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get());
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(jwt.createJWT(anyString())).thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<TeacherDTO> result = loginRestController.verify("b6d6da5c-6a13-4200-b5ad-4e7901d37310--1320354403", request, response);

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(TeacherDTO.class, result.getBody().getClass()),
                () -> assertEquals("Luis Rodriguez", result.getBody().getName()),
                () -> assertEquals("ejemplo@ejemplo.com", result.getBody().getEmail()));

        verify(mailBoxService).isCorrect(anyString(), anyString());
        verify(mailBoxService).getEmailByCode(anyString());
        verify(teacherService).findByEmail(anyString());
        verify(authenticationManager).authenticate(any());
        verify(jwt).createJWT(anyString());
    }

    @Test
    void Should_ReturnTeacher_When_GetTeacherLogged(){
        ReflectionTestUtils.setField(loginRestController, "teacherMapper", teacherMapper);
        Teacher teacher = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(teacher.getEmail(), null,
                teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(teacherService.findByEmail(anyString())).thenReturn(Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get());

        ResponseEntity<TeacherDTO> result = loginRestController.getTeacherLogged();

        assertAll(() -> assertEquals(HttpStatus.OK, result.getStatusCode()),
                () -> assertEquals(TeacherDTO.class, result.getBody().getClass()),
                () -> assertEquals("Luis Rodriguez", result.getBody().getName()),
                () -> assertEquals("ejemplo@ejemplo.com", result.getBody().getEmail()));

        verify(teacherService).findByEmail(anyString());
    }

    @Test
    void Should_ThrowException_When_NoTeacherLogged(){
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            loginRestController.getTeacherLogged();
        });

        assertAll(() -> assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus()),
                () -> assertEquals("Acceso denegado", exception.getMessage()));

        verify(securityContext).getAuthentication();
        verify(teacherService, never()).findByEmail(anyString());
    }
}
