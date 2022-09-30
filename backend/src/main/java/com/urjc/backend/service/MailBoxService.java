package com.urjc.backend.service;

import com.urjc.backend.model.Teacher;
import com.urjc.backend.singleton.CodesEmail;

public interface MailBoxService {
    boolean sendEmail(Long randomCode, Teacher teacher);

    Long generateCodeEmail();

    void addCodeEmail(Long code, String email, String ip);

    CodesEmail getCodesEmails();
}
