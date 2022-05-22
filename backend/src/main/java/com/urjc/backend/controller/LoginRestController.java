package com.urjc.backend.controller;

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
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginRestController {
    //private static final Logger log = LoggerFactory.getLogger(LoginRestController.class);

    private final AuthenticateProvider authenticationManager;

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private TeacherService teacherService;


    @PostMapping(value = "/access")
    public ResponseEntity<String> sendEmailLogin(@RequestBody EmailRequestResponse loginRequest, HttpServletRequest request) {

        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), null);
            authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Usuario no encontrado" ,HttpStatus.NOT_FOUND);
        }

        String ip = request.getRemoteAddr();

        Long randomeCode = mailBoxService.generateCodeEmail();

        mailBoxService.addCodeEmail(randomeCode, loginRequest.getEmail(), ip);

        if(mailBoxService.sendEmail(randomeCode)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("Algo ha salido mal", HttpStatus.BAD_REQUEST);

    }

    @GetMapping(value = "/verify/{code}")
    public ResponseEntity<Object> verify(@PathVariable Long code, HttpServletRequest request, HttpServletResponse response) {

       if(mailBoxService.getCodesEmails().isCorrect(code, request.getRemoteAddr())){
            String email = mailBoxService.getCodesEmails().getEmailByCode(code);

            mailBoxService.getCodesEmails().removeCode(code);
            Teacher teacher = teacherService.findByEmailCurrentCourse(email);

            String token = JWT.createJWT(teacher.getEmail(), teacher.getRoles());

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setDomain("");
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);

           return new ResponseEntity<>(teacher,HttpStatus.OK);
        }
       return new ResponseEntity<>("Código de verificación no encontrado", HttpStatus.NOT_FOUND);
    }
}
