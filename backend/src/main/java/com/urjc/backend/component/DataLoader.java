package com.urjc.backend.component;

import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    @Value("${email.main.admin}")
    private String emailMainAdmin;

    @Value("${name.main.admin}")
    private String nameMainAdmin;

    private TeacherService teacherService;

    @Autowired
    public DataLoader(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    public void run(ApplicationArguments args) {
        Teacher teacher = teacherService.findByEmail(emailMainAdmin);

        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");
        roles.add("TEACHER");

        if(teacher == null) {
            teacherService.save(new Teacher(roles, nameMainAdmin, emailMainAdmin));
        } else if(teacher.getRoles().size() == 1){
            teacher.setRoles(roles);
            teacherService.save(teacher);
        }
    }
}
