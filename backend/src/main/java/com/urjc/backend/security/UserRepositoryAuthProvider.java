package com.urjc.backend.security;

import com.urjc.backend.model.Teacher;
import com.urjc.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepositoryAuthProvider implements AuthenticationProvider {

    @Autowired
    private TeacherRepository teacherService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Teacher teacher = teacherService.findByEmail(authentication.getName());

        if (teacher == null) {
            throw new BadCredentialsException("Docente no encontrado");
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        for (String role : teacher.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role));
        }
        return new UsernamePasswordAuthenticationToken(teacher, null, roles);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
