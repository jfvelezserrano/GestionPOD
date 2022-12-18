package com.urjc.backend.service;

import com.urjc.backend.model.Teacher;

public interface MailBoxService {
    boolean sendEmail(String code, Teacher teacher);

    String generateCodeEmail();

    boolean isCorrect(String code, String ip);

    void addCode(String code, String email, String ip);

    void removeCode(String code);

    String getEmailByCode(String code);

}
