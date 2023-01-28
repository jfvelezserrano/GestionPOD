package com.urjc.backend.service;

import com.urjc.backend.model.Teacher;

import javax.mail.MessagingException;

public interface MailBoxService {
    void sendEmail(String code, Teacher teacher) throws MessagingException;

    String generateCodeEmail();

    boolean isCorrect(String code, String ip);

    void addCode(String code, String email, String ip);

    void removeCode(String code);

    String getEmailByCode(String code);

}
