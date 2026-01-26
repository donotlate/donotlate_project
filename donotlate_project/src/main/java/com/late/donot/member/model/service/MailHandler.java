package com.late.donot.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailHandler {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 작성자 : 유건우
     * 작성일 : 2026-01-21
     * 메일 발송 비동기식
     * 별도의 스레드에서 실행되도록 하여 발송시간 지연 최소화
     */
    @Async
    public void sendMail(MimeMessage mimeMessage) {
        try {
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}