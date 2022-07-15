package com.urjc.backend.service;

import com.urjc.backend.model.Teacher;
import com.urjc.backend.singleton.CodesEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class MailBoxService {

    private final int minCode = 1000000000;
    private final int maxCode = 999999999;

    private CodesEmail codesEmail = CodesEmail.getCodeEmail();

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;


    public boolean sendEmail(Long randomCode, Teacher teacher){
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(teacher.getEmail());
            helper.setFrom("aliciamerinomartinez99@outlook.com");
            helper.setSubject("Acceso a la Gestión POD URJC");

            Map<String, Object> variables = new HashMap<>();
            variables.put("nameTeacher", teacher.getName());
            variables.put("code", randomCode);

            String templateGenerated = this.templateEngine.process("messageMail", new Context(Locale.getDefault(), variables));

            helper.setText(templateGenerated, true);
            emailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long generateCodeEmail(){
        this.codesEmail.removeAllExpiredCodes();

        Long randomNumber = (long)Math.floor(minCode + (Math.random() * maxCode));

        while(getCodesEmails().existsCode(randomNumber)){
            randomNumber = (long)Math.floor(minCode + (Math.random() * maxCode));
        }

        return Long.valueOf(randomNumber);
    }


    public void addCodeEmail(Long code, String email, String ip){
        this.codesEmail.addCode(code, email, ip);
    }

    public CodesEmail getCodesEmails(){
        return this.codesEmail;
    }
}
