package com.urjc.backend.component;

import com.urjc.backend.model.Teacher;
import com.urjc.backend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Configuration
@PropertySource(value = "classpath:/application.properties", encoding="UTF-8")
public class DataLoader implements ApplicationRunner {

    @Value("${email.main.admin}")
    private String emailMainAdmin;

    private final String nameMainAdmin = "Merino Martínez, Alicia";

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

    @Bean
    public PropertiesFactoryBean propertiesfilemapping() {
        PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
        factoryBean.setFileEncoding("UTF-8");
        factoryBean.setLocation(new ClassPathResource("application.properties"));
        return factoryBean;
    }
}
