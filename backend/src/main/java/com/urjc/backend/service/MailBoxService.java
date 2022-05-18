package com.urjc.backend.service;

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

    private final int minCode = 10000000;
    private final int maxCode = 99999999;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;


    public boolean sendEmail(Long randomCode){
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo("a.merinom.2017@alumnos.urjc.es");
            helper.setFrom("aliciamerinomartinez99@outlook.com");
            helper.setSubject("Acceso a la Gestión POD URJC");

            Map<String, Object> variables = new HashMap<>();
            variables.put("nameTeacher", "Alicia Merino Martínez");
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
        Long randomNumber = (long)Math.floor(minCode + (Math.random() * maxCode));

        while(getCodesEmails().existsCode(randomNumber)){
            randomNumber = (long)Math.floor(minCode + (Math.random() * maxCode));
        }

        return Long.valueOf(randomNumber);
    }

    private CodesEmail codesEmail = CodesEmail.getCodeEmail();

    public void addCodeEmail(Long code, String email, String ip){
        this.codesEmail.addCode(code, email, ip);
    }

    public CodesEmail getCodesEmails(){
        return this.codesEmail;
    }
}
