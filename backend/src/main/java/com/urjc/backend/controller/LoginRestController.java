package com.urjc.backend.controller;

import com.urjc.backend.dto.EmailRequestDTO;
import com.urjc.backend.dto.TeacherDTO;
import com.urjc.backend.error.exception.GlobalException;
import com.urjc.backend.mapper.ITeacherMapper;
import com.urjc.backend.model.Teacher;
import com.urjc.backend.security.AuthenticateProvider;
import com.urjc.backend.security.JWT;
import com.urjc.backend.service.MailBoxService;
import com.urjc.backend.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class LoginRestController {

    @Autowired
    private JWT jwt;

    @Autowired
    private AuthenticateProvider authenticationManager;

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ITeacherMapper teacherMapper;


    @PostMapping("/access")
    public ResponseEntity<Void> sendEmailLogin(@RequestBody @Valid EmailRequestDTO loginRequest, HttpServletRequest request) {

        Teacher teacher = teacherService.findIfIsInCurrentCourse(loginRequest.getEmail());

        if(teacher == null){
            throw new GlobalException(HttpStatus.NOT_FOUND, "La dirección de correo es incorrecta");
        }

        String ip = request.getRemoteAddr();
        System.out.println(ip);

        String code = mailBoxService.generateCodeEmail();

        mailBoxService.addCode(code, loginRequest.getEmail(), ip);

        try{
            mailBoxService.sendEmail(code, teacher);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e) {
            throw new GlobalException(HttpStatus.BAD_GATEWAY, "Algo ha fallado. Revise que la dirección de correo es correcta.");
        }
    }

    @GetMapping(value = "/verify/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherDTO> verify(@PathVariable String code, HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRemoteAddr());
       if(mailBoxService.isCorrect(code, request.getRemoteAddr())){
            String email = mailBoxService.getEmailByCode(code);

            mailBoxService.removeCode(code);
            Teacher teacher = teacherService.findByEmail(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
                    teacher.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            authenticationManager.authenticate(authentication);

            String token = jwt.createJWT(teacher.getEmail());

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setDomain("");
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);

            TeacherDTO teacherDTO = teacherMapper.toTeacherDTO(teacher);

           return new ResponseEntity<>(teacherDTO, HttpStatus.OK);
        }
        throw new GlobalException(HttpStatus.UNAUTHORIZED, "Acceso denegado");
    }
}
