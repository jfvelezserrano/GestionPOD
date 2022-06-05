package com.urjc.backend.security;

import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.TeacherService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TeacherService teacherService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            Cookie cookie = WebUtils.getCookie(request, "token");
            if(cookie != null) {
                String token = cookie.getValue();
                if (StringUtils.hasText(token) && (JWT.isCorrect(token)) && (!JWT.isTokenExpired(token))) {
                    Claims claims = JWT.decodeJWT(token);
                    if (claims.getSubject() != null) {
                        Teacher teacher = teacherService.findIfIsInCurrentCourse(claims.getSubject());
                        if (teacher == null) {
                            SecurityContextHolder.clearContext();
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            filterChain.doFilter(request, response);
                            return;
                        } else {
                            Authentication authentication = JWT.getAuthentication(teacher.getRoles(),claims);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            filterChain.doFilter(request, response);
                            return;
                        }
                    }
                }
            }

            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return "/api/access".equals(path) || path.startsWith("/api/verify/");
    }
}