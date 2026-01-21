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

    @Async // 이 메서드는 별도의 스레드에서 실행됩니다.
    public void sendMail(MimeMessage mimeMessage) {
        try {
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}