package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.model.EmailRequestResponse;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.security.JWT;
import com.urjc.backend.security.AuthenticateProvider;
import com.urjc.backend.service.MailBoxService;
import com.urjc.backend.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginRestController {
    //private static final Logger log = LoggerFactory.getLogger(LoginRestController.class);

    interface TeacherLogin extends Teacher.Base, Teacher.Roles {
    }

    @Autowired
    private AuthenticateProvider authenticationManager;

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private TeacherService teacherService;


    @PostMapping(value = "/access")
    public ResponseEntity<?> sendEmailLogin(@RequestBody EmailRequestResponse loginRequest, HttpServletRequest request) {

        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), null);
            authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String ip = request.getRemoteAddr();

        Long randomCode = mailBoxService.generateCodeEmail();

        mailBoxService.addCodeEmail(randomCode, loginRequest.getEmail(), ip);

        Teacher teacher = teacherService.findByEmail(loginRequest.getEmail());

        try{
            mailBoxService.sendEmail(randomCode, teacher);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @JsonView(TeacherLogin.class)
    @GetMapping(value = "/verify/{code}")
    public ResponseEntity<?> verify(@PathVariable Long code, HttpServletRequest request, HttpServletResponse response) {

       if(mailBoxService.getCodesEmails().isCorrect(code, request.getRemoteAddr())){
            String email = mailBoxService.getCodesEmails().getEmailByCode(code);

            mailBoxService.getCodesEmails().removeCode(code);
            Teacher teacher = teacherService.findByEmail(email);

            String token = JWT.createJWT(teacher.getEmail());

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setDomain("");
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);

           return new ResponseEntity<>(teacher,HttpStatus.OK);
        }
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
