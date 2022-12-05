package com.urjc.backend.service;

import com.urjc.backend.model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MailBoxServiceImpl implements MailBoxService {

    @Value("#{T(java.lang.Integer).parseInt('${mail.minimum.number.code}')}")
    private int minCode;

    @Value("#{T(java.lang.Integer).parseInt('${mail.maximum.number.code}')}")
    private int maxCode;

    @Value("${spring.mail.username}")
    private String emailFrom;

    private Map<Long, List<String>> codesMap = new HashMap<>();

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public boolean sendEmail(Long randomCode, Teacher teacher) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(teacher.getEmail());
            helper.setFrom(emailFrom);
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
    public Long generateCodeEmail() {
        removeAllExpiredCodes();

        Long randomNumber = (long) Math.floor(minCode + (Math.random() * maxCode));

        while (existsCode(randomNumber)) {
            randomNumber = (long) Math.floor(minCode + (Math.random() * maxCode));
        }

        return Long.valueOf(randomNumber);
    }

    public Boolean isCorrect(Long code, String ip) {
        if (existsCode(code)) {
            List<String> values = this.codesMap.get(code);

            return (values.get(1).equals(ip) && !isDateExpired(values.get(2)));
        }
        return false;
    }

    public void addCode(Long code, String email, String ip) {
        if (!existsCode(code)) {
            this.codesMap.put(code, new ArrayList<>());
            this.codesMap.get(code).add(email);
            this.codesMap.get(code).add(ip);
            this.codesMap.get(code).add(generateExpirationDate());
        }
    }

    public void removeCode(Long code) {
        this.codesMap.remove(code);
    }

    public String getEmailByCode(Long code) {
        return this.codesMap.get(code).get(0);
    }

    private String getDateExpirationByCode(Long code) {
        return this.codesMap.get(code).get(2);
    }

    private void removeAllExpiredCodes() {
        Iterator<Long> iterator = this.codesMap.keySet().iterator();
        while (iterator.hasNext()) {
            Long code = iterator.next();
            if (isDateExpired(getDateExpirationByCode(code))) {
                removeCode(code);
            }
        }
    }

    private Boolean existsCode(Long code) {
        return this.codesMap.get(code) != null;
    }

    private boolean isDateExpired(String dateCodeEmail) {
        boolean isExpired;
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = dateFormat.parse(dateCodeEmail);
            Date currentDate = new Date();
            isExpired = date.before(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
            isExpired = true;
        }

        return isExpired;
    }

    private String generateExpirationDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        return dateFormat.format(date);
    }
}
