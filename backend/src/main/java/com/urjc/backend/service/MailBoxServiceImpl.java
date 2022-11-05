package com.urjc.backend.service;

import com.urjc.backend.model.Teacher;
import com.urjc.backend.singleton.CodesEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class MailBoxServiceImpl implements MailBoxService {

    @Value("#{T(java.lang.Integer).parseInt('${mail.minimum.number.code}')}")
    private int minCode;

    @Value("#{T(java.lang.Integer).parseInt('${mail.maximum.number.code}')}")
    private int maxCode;

    private CodesEmail codesEmail = CodesEmail.getCodeEmail();

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
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

    @Override
    public Long generateCodeEmail(){
        this.codesEmail.removeAllExpiredCodes();

        Long randomNumber = (long)Math.floor(minCode + (Math.random() * maxCode));

        while(getCodesEmails().existsCode(randomNumber)){
            randomNumber = (long)Math.floor(minCode + (Math.random() * maxCode));
        }

        return Long.valueOf(randomNumber);
    }

    @Override
    public void addCodeEmail(Long code, String email, String ip){
        this.codesEmail.addCode(code, email, ip);
    }

    @Override
    public CodesEmail getCodesEmails(){
        return this.codesEmail;
    }
}
