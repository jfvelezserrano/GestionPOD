package com.urjc.backend.component;

import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    private TeacherService teacherService;

    @Autowired
    public DataLoader(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    public void run(ApplicationArguments args) {
        Teacher teacher = teacherService.findByEmail("a.merinom.2017@alumnos.urjc.es");
        if(teacher == null) {
            List<String> roles = new ArrayList<>();
            roles.add("ADMIN");
            roles.add("TEACHER");
            teacherService.saveTeacher(new Teacher(roles, "Alicia Merino Martínez", "a.merinom.2017@alumnos.urjc.es"));
        }
    }
}
