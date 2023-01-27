package com.urjc.backend.service;

import com.urjc.backend.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailBoxServiceTest {

    private static final String emailFrom = "a.merinom.2017@alumnos.urjc.es";

    @InjectMocks
    MailBoxServiceImpl mailBoxService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private ITemplateEngine templateEngine;

    @Test
    void Should_CreateAndSendEmail_When_SendEmail() throws MessagingException {
        ReflectionTestUtils.setField(mailBoxService, "emailFrom", emailFrom);
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("messageMail"), any(Context.class))).thenReturn("Mensaje");
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        mailBoxService.sendEmail("86456431", Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get());
        verify(javaMailSender).createMimeMessage();
        verify(templateEngine).process(eq("messageMail"), any(Context.class));
        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void Should_CreateSecureCode_When_GenerateCodeEmail() {
        String result = mailBoxService.generateCodeEmail();
        System.out.println(result);
        Pattern pattern = Pattern.compile("(\\w){8}-(\\w){4}-(\\w){4}-(\\w){4}-(\\w){8,12}-{1,2}(\\d)*");
        assertAll(() -> assertTrue(pattern.matcher(result).matches()));
    }

    @Test
    void Should_RemoveExpiredCodes_When_GenerateCodeEmail() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10));
        String resultDate = dateFormat.format(date);

        String code = mailBoxService.generateCodeEmail();
        mailBoxService.getCodesMap().put(code, new ArrayList<>());
        mailBoxService.getCodesMap().get(code).add("ejemplo@ejemplo.com");
        mailBoxService.getCodesMap().get(code).add("192.168.0.1");
        mailBoxService.getCodesMap().get(code).add(resultDate);

        mailBoxService.generateCodeEmail();
        assertAll(() -> assertTrue(mailBoxService.getCodesMap().isEmpty()));

    }

    @Test
    void Should_ReturnTrue_When_CheckIfIsCorrect() {
        mailBoxService.addCode("62fbab16-0016-4ff1-ad75-8bedea58a8e3--1717373416", "ejemplo@ejemplo.com", "192.168.0.1");
        boolean result = mailBoxService.isCorrect("62fbab16-0016-4ff1-ad75-8bedea58a8e3--1717373416", "192.168.0.1");
        assertAll(() -> assertTrue(result));
    }

    @Test
    void Should_ReturnFalse_When_CheckIfIsCorrectAndNotExists() {
        boolean result = mailBoxService.isCorrect("62fbab16-0016-4ff1-ad75-8bedea58a8e3--1717373416", "192.168.0.1");
        assertAll(() -> assertFalse(result));
    }

    @Test
    void Should_ExistsCode_When_AddCode() {
        mailBoxService.addCode("62fbab16-0016-4ff1-ad75-8bedea58a8e3--1717373416", "ejemplo@ejemplo.com", "192.168.0.1");
        List<String> result = mailBoxService.getCodesMap().get("62fbab16-0016-4ff1-ad75-8bedea58a8e3--1717373416");
        assertAll(() -> assertEquals("ejemplo@ejemplo.com", result.get(0)),
                () -> assertEquals("192.168.0.1", result.get(1)));
    }

    @Test
    void Should_NotExistsCode_When_RemoveCode() {
        mailBoxService.removeCode("62fbab16-0016-4ff1-ad75-8bedea58a8e3--1717373416");
        List<String> result = mailBoxService.getCodesMap().get("62fbab16-0016-4ff1-ad75-8bedea58a8e3--1717373416");
        assertAll(() -> assertNull(result));
    }

    @Test
    void Should_FoundEmail_When_GetEmailByCode() {
        mailBoxService.addCode("62fbab16-0016-4ff1-ad75-8bedea58a8e3--1717373416", "ejemplo@ejemplo.com", "192.168.0.1");
        String result = mailBoxService.getEmailByCode("62fbab16-0016-4ff1-ad75-8bedea58a8e3--1717373416");
        assertAll(() -> assertEquals("ejemplo@ejemplo.com", result));
    }
}
