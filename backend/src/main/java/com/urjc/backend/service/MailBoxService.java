package com.urjc.backend.service;

import com.urjc.backend.model.Teacher;

public interface MailBoxService {
    boolean sendEmail(Long randomCode, Teacher teacher);

    Long generateCodeEmail();

    Boolean isCorrect(Long code, String ip);

    void addCode(Long code, String email, String ip);

    void removeCode(Long code);

    String getEmailByCode(Long code);

}
