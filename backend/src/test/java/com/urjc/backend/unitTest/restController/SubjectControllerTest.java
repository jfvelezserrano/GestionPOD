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
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;
import java.util.*;


@RunWith(MockitoJUnitRunner.class)
public class SubjectControllerTest {

    private Cookie cookie;

    @Autowired
    JWT jwt;

    @Value("${email.main.admin}")
    private String emailMainAdmin;

    @InjectMocks
    SubjectRestController subjectRestController;

    @Mock
    SubjectService subjectService;

    @Mock
    CourseService courseService;

    @BeforeEach
    public void addCookie(){
        String token = jwt.createJWT(emailMainAdmin);
        this.cookie = new Cookie("token", token);
    }

}
