package com.urjc.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.urjc.backend.dto.EmailRequestDTO;
import com.urjc.backend.dto.TeacherDTO;
import com.urjc.backend.mapper.ITeacherMapper;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginRestController {

    @Autowired
    private AuthenticateProvider authenticationManager;

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    ITeacherMapper teacherMapper;


    @PostMapping(value = "/access")
    public ResponseEntity<Void> sendEmailLogin(@RequestBody EmailRequestDTO loginRequest, HttpServletRequest request) {

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

    @GetMapping(value = "/verify/{code}")
    public ResponseEntity<TeacherDTO> verify(@PathVariable Long code, HttpServletRequest request, HttpServletResponse response) {

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

            TeacherDTO teacherDTO = teacherMapper.toTeacherDTO(teacher);

           return new ResponseEntity<>(teacherDTO,HttpStatus.OK);
        }
       return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping(value = "/teacherLogged")
    public ResponseEntity<TeacherDTO> getTeacherLogged() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Teacher teacherLogged = teacherService.findByEmail(authentication.getName());

        return new ResponseEntity<>(teacherMapper.toTeacherDTO(teacherLogged),HttpStatus.OK);
    }
}
